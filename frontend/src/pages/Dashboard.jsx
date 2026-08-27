import { useEffect, useMemo, useState } from "react";
import client from "../api/client";
import { lastNDays, todayIso, formatShort, formatWeekday, formatLong } from "../utils/date";

export default function Dashboard() {
  const [habits, setHabits] = useState([]);
  const [entries, setEntries] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const days = useMemo(() => lastNDays(7), []);
  const today = todayIso();

  async function loadData() {
    setLoading(true);
    setError("");
    try {
      const { data } = await client.get("/habits/logs", {
        params: { from: days[0], to: days[days.length - 1] },
      });
      setHabits(data.habits.filter((h) => h.active));
      setEntries(data.entries || {});
    } catch (err) {
      setError("Nije moguće učitati podatke.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  function isCompleted(habitId, date) {
    return Boolean(entries[date]?.[habitId]);
  }

  async function toggle(habitId, date, currentlyCompleted) {
    // optimistic update
    setEntries((prev) => {
      const next = { ...prev };
      next[date] = { ...(next[date] || {}) };
      next[date][habitId] = !currentlyCompleted;
      return next;
    });

    try {
      await client.post("/habits/logs/toggle", {
        habitId,
        date,
        completed: !currentlyCompleted,
      });
    } catch (err) {
      // revert on failure
      setEntries((prev) => {
        const next = { ...prev };
        next[date] = { ...(next[date] || {}) };
        next[date][habitId] = currentlyCompleted;
        return next;
      });
    }
  }

  const todayCompletedCount = habits.filter((h) => isCompleted(h.id, today)).length;
  const todayPercent = habits.length > 0 ? Math.round((todayCompletedCount / habits.length) * 100) : 0;

  if (loading) return <div className="page-loading">Učitavanje...</div>;

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1>Danas</h1>
          <p className="text-muted">{formatLong(today)}</p>
        </div>
        <div className="progress-badge">
          <div className="progress-ring" style={{ "--percent": todayPercent }}>
            <span>{todayPercent}%</span>
          </div>
          <span className="text-muted">
            {todayCompletedCount}/{habits.length} navika
          </span>
        </div>
      </div>

      {error && <div className="alert-error">{error}</div>}

      {habits.length === 0 ? (
        <div className="empty-state">
          Nemaš još nijednu naviku. Dodaj ih na stranici{" "}
          <a href="/habits">Navike</a>.
        </div>
      ) : (
        <div className="table-wrapper">
          <table className="habit-grid">
            <thead>
              <tr>
                <th className="habit-name-col">Navika</th>
                {days.map((d) => (
                  <th key={d} className={d === today ? "col-today" : ""}>
                    <div className="day-header">
                      <span className="day-weekday">{formatWeekday(d)}</span>
                      <span className="day-date">{formatShort(d)}</span>
                    </div>
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {habits.map((habit) => (
                <tr key={habit.id}>
                  <td className="habit-name-col">
                    {habit.icon ? `${habit.icon} ` : ""}
                    {habit.name}
                  </td>
                  {days.map((d) => {
                    const completed = isCompleted(habit.id, d);
                    return (
                      <td key={d} className={d === today ? "col-today" : ""}>
                        <button
                          className={`check-cell ${completed ? "checked" : ""}`}
                          onClick={() => toggle(habit.id, d, completed)}
                          aria-label={`${habit.name} - ${d}`}
                        >
                          {completed ? "✓" : ""}
                        </button>
                      </td>
                    );
                  })}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
