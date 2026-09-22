const form = document.querySelector("#form");
const input = document.querySelector("#input");
const chat = document.querySelector("#chat");

function addMessage(text, type) {

  const div = document.createElement("div");

  div.className = `message ${type}`;

  div.textContent = text;

  chat.appendChild(div);

  chat.scrollTop = chat.scrollHeight;

  return div;
}

form.addEventListener("submit", async (event) => {

  event.preventDefault();

  const message = input.value.trim();

  if (!message) return;

  input.value = "";

  addMessage(message, "user");

  const thinking = addMessage(
    "JARVIS is thinking...",
    "jarvis"
  );

  try {

    const response = await fetch("/api/chat", {

      method: "POST",

      headers: {
        "Content-Type": "application/json"
      },

      body: JSON.stringify({
        message
      })

    });

    const data = await response.json();

    if (!response.ok) {
      throw new Error(data.error);
    }

    thinking.textContent = data.reply;

  } catch (error) {

    thinking.textContent =
      "Error: " + error.message;

  }

});
