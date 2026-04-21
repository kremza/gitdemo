const btn = document.querySelector(".btn");

btn.addEventListener("mousemove", (e) => {
  const rect = btn.getBoundingClientRect();
  const x = e.clientX - rect.left;
  const y = e.clientY - rect.top;

  btn.style.setProperty("--mouseX", `${x}px`);
  btn.style.setProperty("--mouseY", `${y}px`);
});