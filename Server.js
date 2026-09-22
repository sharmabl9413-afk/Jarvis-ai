import "dotenv/config";
import express from "express";
import OpenAI from "openai";
import { exec } from "child_process";

const app = express();
const PORT = process.env.PORT || 3000;

app.use(express.json());
app.use(express.static("public"));

const client = new OpenAI({
  apiKey: process.env.OPENAI_API_KEY
});

// Safe PC commands only
const commands = {
  open_notepad: () => exec("notepad.exe"),
  open_calculator: () => exec("calc.exe"),
  open_browser: () => exec("start https://www.google.com")
};

app.post("/api/action", (req, res) => {
  const { action } = req.body;

  if (!commands[action]) {
    return res.status(400).json({
      error: "Command not allowed"
    });
  }

  commands[action]();

  res.json({
    success: true,
    action
  });
});

app.post("/api/chat", async (req, res) => {
  try {
    const message = String(req.body.message || "").trim();

    if (!message) {
      return res.status(400).json({
        error: "Message required"
      });
    }

    const response = await client.responses.create({
      model: "gpt-5.6-luna",

      tools: [
        {
          type: "web_search"
        }
      ],

      instructions: `
You are JARVIS, a helpful personal AI assistant.

Reply naturally in Hindi/Hinglish when the user uses Hindi/Hinglish.

You can provide current information using web search.

Never pretend that you performed an action if you did not.
For PC control, only use explicitly allowed local actions.
Do not execute arbitrary shell commands.
`,

      input: message
    });

    res.json({
      reply: response.output_text
    });

  } catch (error) {
    console.error(error);

    res.status(500).json({
      error: "JARVIS error"
    });
  }
});

app.listen(PORT, () => {
  console.log(`JARVIS running on http://localhost:${PORT}`);
});
