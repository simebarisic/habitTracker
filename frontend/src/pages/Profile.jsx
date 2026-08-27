import { useEffect, useState } from "react";
import client from "../api/client";
import { useAuth } from "../context/AuthContext";

export default function Profile() {
  const { updateProfile } = useAuth();
  const [currentEmail, setCurrentEmail] = useState("");
  const [currentPassword, setCurrentPassword] = useState("");
  const [newUsername, setNewUsername] = useState("");
  const [newEmail, setNewEmail] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmNewPassword, setConfirmNewPassword] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [saving, setSaving] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function load() {
      try {
        const { data } = await client.get("/users/me");
        setNewUsername(data.username);
        setNewEmail(data.email);
        setCurrentEmail(data.email);
      } catch (err) {
        setError("Nije moguće učitati profil.");
      } finally {
        setLoading(false);
      }
    }
    load();
  }, []);

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setSuccess("");

    if (newPassword && newPassword !== confirmNewPassword) {
      setError("Nova lozinka i potvrda se ne podudaraju.");
      return;
    }

    setSaving(true);
    try {
      await updateProfile({
        currentPassword,
        newUsername: newUsername,
        newEmail: newEmail !== currentEmail ? newEmail : null,
        newPassword: newPassword || null,
      });
      setSuccess("Profil je uspješno ažuriran.");
      setCurrentPassword("");
      setNewPassword("");
      setConfirmNewPassword("");
      if (newEmail) setCurrentEmail(newEmail);
    } catch (err) {
      setError(err.response?.data?.message || "Ažuriranje nije uspjelo.");
    } finally {
      setSaving(false);
    }
  }

  if (loading) return <div className="page-loading">Učitavanje...</div>;

  return (
    <div className="page">
      <div className="page-header">
        <h1>Postavke profila</h1>
      </div>

      <div className="card" style={{ maxWidth: 440 }}>
        {error && <div className="alert-error">{error}</div>}
        {success && <div className="alert-success">{success}</div>}

        <form onSubmit={handleSubmit} className="profile-form">
          <label>
            Korisničko ime
            <input
              type="text"
              value={newUsername}
              onChange={(e) => setNewUsername(e.target.value)}
              minLength={3}
              required
            />
          </label>

          <label>
            Email
            <input
              type="email"
              value={newEmail}
              onChange={(e) => setNewEmail(e.target.value)}
              required
            />
          </label>

          <hr className="profile-divider" />

          <label>
            Nova lozinka <span className="text-muted">(ostavi prazno ako je ne mijenjaš)</span>
            <input
              type="password"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              minLength={6}
              placeholder="••••••••"
            />
          </label>

          {newPassword && (
            <label>
              Potvrdi novu lozinku
              <input
                type="password"
                value={confirmNewPassword}
                onChange={(e) => setConfirmNewPassword(e.target.value)}
                minLength={6}
              />
            </label>
          )}

          <hr className="profile-divider" />

          <label>
            Trenutna lozinka <span className="text-muted">(obavezno za potvrdu promjene)</span>
            <input
              type="password"
              value={currentPassword}
              onChange={(e) => setCurrentPassword(e.target.value)}
              required
              placeholder="••••••••"
            />
          </label>

          <button type="submit" className="btn-primary" disabled={saving}>
            {saving ? "Spremanje..." : "Spremi promjene"}
          </button>
        </form>
      </div>
    </div>
  );
}
