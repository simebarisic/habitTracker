export function toIsoDate(date) {
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
}

export function todayIso() {
  return toIsoDate(new Date());
}

export function addDays(isoDate, days) {
  const d = new Date(isoDate + "T00:00:00");
  d.setDate(d.getDate() + days);
  return toIsoDate(d);
}

export function lastNDays(n, endIso = todayIso()) {
  const dates = [];
  for (let i = n - 1; i >= 0; i--) {
    dates.push(addDays(endIso, -i));
  }
  return dates;
}

export function formatShort(isoDate) {
  const d = new Date(isoDate + "T00:00:00");
  return d.toLocaleDateString("hr-HR", { day: "2-digit", month: "2-digit" });
}

export function formatWeekday(isoDate) {
  const d = new Date(isoDate + "T00:00:00");
  return d.toLocaleDateString("hr-HR", { weekday: "short" });
}

export function formatLong(isoDate) {
  const d = new Date(isoDate + "T00:00:00");
  return d.toLocaleDateString("hr-HR", {
    weekday: "long",
    day: "numeric",
    month: "long",
    year: "numeric",
  });
}
