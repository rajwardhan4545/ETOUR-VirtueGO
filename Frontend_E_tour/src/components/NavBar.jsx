import { Link } from "react-router-dom";

const Navbar = () => {
  return (
    <nav style={styles.nav}>
      <h2>E-Tour</h2>
      <div>
        <Link to="/">Home</Link>
        <Link to="/tours">Tours</Link>
        <Link to="/login">Login</Link>
        <Link to="/register">Register</Link>
      </div>
    </nav>
  );
};

const styles = {
  nav: {
    display: "flex",
    justifyContent: "space-between",
    padding: "15px",
    background: "#0369a1", // sky-700
    color: "white"
  }
};

export default Navbar;
