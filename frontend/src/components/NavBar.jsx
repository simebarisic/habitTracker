import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function NavBar() {
  const { username, logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate("/login");
  }

  return (
    <header className="navbar">
      <div className="navbar-brand">🔥 Habit Tracker</div>
      <nav className="navbar-links">
        <NavLink to="/" end>
          Danas
        </NavLink>
        <NavLink to="/history">Povijest</NavLink>
        <NavLink to="/habits">Navike</NavLink>
      </nav>
      <div className="navbar-user">
        <NavLink to="/profile" className="navbar-username">
          {username}
        </NavLink>
        <button onClick={handleLogout} className="btn-link">
          Odjava
        </button>
      </div>
    </header>
  );
}
