import { useEffect, useState } from "react";
import client from "../api/client";

export default function Habits() {
  const [habits, setHabits] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [newName, setNewName] = useState("");
  const [newIcon, setNewIcon] = useState("");
  const [saving, setSaving] = useState(false);

  async function loadHabits() {
    setLoading(true);
    setError("");
    try {
      const { data } = await client.get("/habits", { params: { includeInactive: true } });
      setHabits(data);
    } catch (err) {
      setError("Nije moguće učitati navike.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadHabits();
  }, []);

  async function handleCreate(e) {
    e.preventDefault();
    if (!newName.trim()) return;
    setSaving(true);
    setError("");
    try {
      await client.post("/habits", { name: newName.trim(), icon: newIcon.trim() || null });
      setNewName("");
      setNewIcon("");
      await loadHabits();
    } catch (err) {
      setError(err.response?.data?.message || "Nije moguće dodati naviku.");
    } finally {
      setSaving(false);
    }
  }

  async function toggleActive(habit) {
    try {
      await client.put(`/habits/${habit.id}`, {
        name: habit.name,
        icon: habit.icon,
        sortOrder: habit.sortOrder,
        active: !habit.active,
      });
      await loadHabits();
    } catch (err) {
      setError("Nije moguće ažurirati naviku.");
    }
  }

  async function handleDelete(habit) {
    if (!window.confirm(`Trajno obrisati naviku "${habit.name}" i svu njenu povijest?`)) return;
    try {
      await client.delete(`/habits/${habit.id}`);
      await loadHabits();
    } catch (err) {
      setError("Nije moguće obrisati naviku.");
    }
  }

  if (loading) return <div className="page-loading">Učitavanje...</div>;

  return (
    <div className="page">
      <div className="page-header">
        <h1>Navike</h1>
      </div>

      {error && <div className="alert-error">{error}</div>}

      <form className="habit-form" onSubmit={handleCreate}>
        <input
          type="text"
          placeholder="Naziv nove navike (npr. Trčanje)"
          value={newName}
          onChange={(e) => setNewName(e.target.value)}
          required
        />
        <input
          type="text"
          placeholder="Emoji (opcionalno)"
          value={newIcon}
          onChange={(e) => setNewIcon(e.target.value)}
          maxLength={4}
          className="icon-input"
        />
        <button type="submit" className="btn-primary" disabled={saving}>
          {saving ? "Dodavanje..." : "+ Dodaj naviku"}
        </button>
      </form>

      <ul className="habit-list">
        {habits.map((habit) => (
          <li key={habit.id} className={`habit-list-item ${!habit.active ? "inactive" : ""}`}>
            <span className="habit-list-name">
              {habit.icon ? `${habit.icon} ` : ""}
              {habit.name}
            </span>
            <div className="habit-list-actions">
              <button className="btn-secondary" onClick={() => toggleActive(habit)}>
                {habit.active ? "Deaktiviraj" : "Aktiviraj"}
              </button>
              <button className="btn-danger" onClick={() => handleDelete(habit)}>
                Obriši
              </button>
            </div>
          </li>
        ))}
      </ul>

      {habits.length === 0 && (
        <p className="text-muted">Još nema navika. Dodaj prvu iznad.</p>
      )}
    </div>
  );
}
