// app/not-found.tsx
export default function NotFound() {
  return (
    <div style={{ padding: "4rem", textAlign: "center" }}>
      <h1 style={{ fontSize: "2rem" }}>404 – Page Not Found</h1>
      <p style={{ marginTop: "1rem" }}>
        Sorry, we couldn’t find the page you’re looking for.
      </p>
      <a
        href="/"
        style={{
          display: "inline-block",
          marginTop: "2rem",
          padding: "0.75rem 1.5rem",
          background: "#0070f3",
          color: "#fff",
          textDecoration: "none",
          borderRadius: "6px",
        }}
      >
        ← Go back home
      </a>
    </div>
  );
}
