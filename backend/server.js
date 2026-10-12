import express from "express";
import { Client } from "@notionhq/client";
import crypto from "node:crypto";

const app = express();
app.disable("x-powered-by");

const { NOTION_TOKEN, WIDGET_API_KEY } = process.env;
const notion = NOTION_TOKEN ? new Client({ auth: NOTION_TOKEN }) : null;
const port = Number(process.env.PORT || 3000);

function authorized(req) {
  const supplied = req.get("x-widget-key") || "";
  const expected = WIDGET_API_KEY || "";
  if (!supplied || !expected) return false;
  const a = Buffer.from(supplied);
  const b = Buffer.from(expected);
  return a.length === b.length && crypto.timingSafeEqual(a, b);
}

function textValue(value) {
  if (!value) return "";
  if (Array.isArray(value)) return value.map(x => x.plain_text || "").join("");
  return String(value);
}

function propertyText(prop) {
  if (!prop) return "";
  if (prop.type === "title" || prop.type === "rich_text") return textValue(prop[prop.type]);
  if (prop.type === "status" || prop.type === "select") return prop[prop.type]?.name || "";
  if (prop.type === "date") return prop.date?.start || "";
  if (prop.type === "checkbox") return prop.checkbox ? "true" : "false";
  if (prop.type === "number") return prop.number == null ? "" : String(prop.number);
  if (prop.type === "url") return prop.url || "";
  return "";
}

function mapPage(page, kind) {
  const props = page.properties || {};
  const entries = Object.entries(props);
  const titleEntry = entries.find(([, p]) => p.type === "title") || entries.find(([name]) => /name|title|task|habit|goal|event/i.test(name));
  const titleProp = titleEntry?.[1];
  const title = titleProp?.type === "title" ? textValue(titleProp.title) : propertyText(titleProp);
  const dateEntry = entries.find(([name, p]) => p.type === "date" && /date|due|start|when/i.test(name)) || entries.find(([, p]) => p.type === "date");
  const doneEntry = entries.find(([name, p]) => p.type === "checkbox" && /done|complete|finished|checked/i.test(name));
  const statusEntry = entries.find(([name, p]) => /status/i.test(name) && ["status", "select"].includes(p.type));
  const detailEntry = entries.find(([name, p]) => name !== titleEntry?.[0] && ["rich_text", "select", "status"].includes(p.type));
  const result = { title: title || "Untitled" };
  if (kind === "tasks" || kind === "habits") {
    const rawDone = doneEntry ? doneEntry[1].checkbox : false;
    const state = statusEntry ? propertyText(statusEntry[1]).toLowerCase() : "";
    result.completed = Boolean(rawDone) || ["done", "complete", "completed"].includes(state);
  }
  if (kind === "goals") result.detail = detailEntry ? propertyText(detailEntry[1]) : "";
  if (kind === "calendar") result.date = dateEntry ? propertyText(dateEntry[1]) : "";
  return result;
}

async function readDatabase(id, kind) {
  if (!id) return [];
  const pages = [];
  let cursor;
  do {
    const response = await notion.databases.query({
      database_id: id,
      page_size: 100,
      ...(cursor ? { start_cursor: cursor } : {})
    });
    pages.push(...response.results.map(page => mapPage(page, kind)));
    cursor = response.has_more ? response.next_cursor : undefined;
  } while (cursor && pages.length < 200);
  return pages.slice(0, 30);
}

app.get("/health", (_req, res) => res.json({ ok: true, configured: Boolean(notion && WIDGET_API_KEY) }));

app.get("/dashboard", async (req, res) => {
  if (!authorized(req)) return res.status(401).json({ error: "Unauthorized: configure the widget API key." });
  if (!notion) return res.status(503).json({ error: "Notion backend is not configured." });
  try {
    const [tasks, goals, habits, calendar] = await Promise.all([
      readDatabase(process.env.TASKS_DATABASE_ID, "tasks"),
      readDatabase(process.env.GOALS_DATABASE_ID, "goals"),
      readDatabase(process.env.HABITS_DATABASE_ID, "habits"),
      readDatabase(process.env.CALENDAR_DATABASE_ID, "calendar")
    ]);
    res.set("Cache-Control", "private, no-store");
    res.json({ tasks, goals, habits, calendar });
  } catch (error) {
    console.error("Dashboard sync failed:", error?.message || error);
    res.status(502).json({ error: "Could not read Notion databases. Check integration access and database IDs." });
  }
});

app.listen(port, "0.0.0.0", () => console.log(`Notion widget backend listening on ${port}`));
