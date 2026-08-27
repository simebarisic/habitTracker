import { useEffect, useState } from "react";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from "recharts";
import client from "../api/client";
import { addDays, todayIso, formatShort } from "../utils/date";

const RANGE_OPTIONS = [
  { label: "7 dana", days: 7 },
  { label: "30 dana", days: 30 },
  { label: "90 dana", days: 90 },
  { label: "Cijela godina", days: 365 },
];

export default function History() {
  const [rangeDays, setRangeDays] = useState(30);
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function load() {
      setLoading(true);
      setError("");
      const to = todayIso();
      const from = addDays(to, -(rangeDays - 1));
      try {
        const { data } = await client.get("/stats", { params: { from, to } });
        setStats(data);
      } catch (err) {
        setError("Nije moguće učitati statistiku.");
      } finally {
        setLoading(false);
      }
    }
    load();
  }, [rangeDays]);

  const chartData =
    stats?.dailyProgress.map((d) => ({
      date: formatShort(d.date),
      percent: d.percent,
    })) || [];

  return (
    <div className="page">
      <div className="page-header">
        <h1>Povijest</h1>
        <div className="range-selector">
          {RANGE_OPTIONS.map((opt) => (
            <button
              key={opt.days}
              className={`chip ${rangeDays === opt.days ? "chip-active" : ""}`}
              onClick={() => setRangeDays(opt.days)}
            >
              {opt.label}
            </button>
          ))}
        </div>
      </div>

      {error && <div className="alert-error">{error}</div>}

      {loading ? (
        <div className="page-loading">Učitavanje...</div>
      ) : (
        <>
          <div className="card">
            <h2>Dnevni napredak</h2>
            <ResponsiveContainer width="100%" height={280}>
              <LineChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#2a2f3a" />
                <XAxis dataKey="date" stroke="#8891a3" tick={{ fontSize: 12 }} />
                <YAxis
                  stroke="#8891a3"
                  tick={{ fontSize: 12 }}
                  domain={[0, 100]}
                  unit="%"
                />
                <Tooltip
                  contentStyle={{
                    background: "#1a1d24",
                    border: "1px solid #2a2f3a",
                    borderRadius: 8,
                  }}
                  formatter={(value) => [`${value}%`, "Napredak"]}
                />
                <Line
                  type="monotone"
                  dataKey="percent"
                  stroke="#f97316"
                  strokeWidth={2}
                  dot={false}
                />
              </LineChart>
            </ResponsiveContainer>
          </div>

          <div className="card">
            <h2>Statistika po navici</h2>
            <div className="table-wrapper">
              <table className="stats-table">
                <thead>
                  <tr>
                    <th>Navika</th>
                    <th>Ukupno</th>
                    <th>Trenutni niz</th>
                    <th>Najdulji niz</th>
                    <th>Stopa uspješnosti</th>
                  </tr>
                </thead>
                <tbody>
                  {stats?.habitStats.map((s) => (
                    <tr key={s.habitId}>
                      <td>{s.name}</td>
                      <td>{s.totalCompletions}</td>
                      <td>{s.currentStreak > 0 ? `🔥 ${s.currentStreak}` : "-"}</td>
                      <td>{s.longestStreak}</td>
                      <td>
                        <div className="rate-bar">
                          <div
                            className="rate-bar-fill"
                            style={{ width: `${Math.min(s.completionRatePercent, 100)}%` }}
                          />
                          <span>{s.completionRatePercent}%</span>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </>
      )}
    </div>
  );
}
