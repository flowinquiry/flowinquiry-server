import type { Config } from "tailwindcss";

const config = {
  darkMode: ["class"],
  content: [
    "./pages/**/*.{ts,tsx}",
    "./components/**/*.{ts,tsx}",
    "./@/components/**/*.{ts,tsx}",
    "./providers/**/*.{ts,tsx}",
    "./app/**/*.{ts,tsx}",
    "./src/**/*.{ts,tsx}",
  ],
  prefix: "",
  theme: {
    container: {
      center: true,
      padding: "2rem",
      screens: {
        "2xl": "1800px",
      },
    },
    extend: {
      colors: {
        border: "hsl(var(--border))",
        input: "hsl(var(--input))",
        ring: "hsl(var(--ring))",
        background: "hsl(var(--background))",
        foreground: "hsl(var(--foreground))",
        primary: {
          DEFAULT: "hsl(var(--primary))",
          foreground: "hsl(var(--primary-foreground))",
        },
        secondary: {
          DEFAULT: "hsl(var(--secondary))",
          foreground: "hsl(var(--secondary-foreground))",
        },
        destructive: {
          DEFAULT: "hsl(var(--destructive))",
          foreground: "hsl(var(--destructive-foreground))",
        },
        muted: {
          DEFAULT: "hsl(var(--muted))",
          foreground: "hsl(var(--muted-foreground))",
        },
        accent: {
          DEFAULT: "hsl(var(--accent))",
          foreground: "hsl(var(--accent-foreground))",
        },
        popover: {
          DEFAULT: "hsl(var(--popover))",
          foreground: "hsl(var(--popover-foreground))",
        },
        card: {
          DEFAULT: "hsl(var(--card))",
          foreground: "hsl(var(--card-foreground))",
        },
        sidebar: {
          DEFAULT: "hsl(var(--sidebar-background))",
          foreground: "hsl(var(--sidebar-foreground))",
          primary: "hsl(var(--sidebar-primary))",
          "primary-foreground": "hsl(var(--sidebar-primary-foreground))",
          accent: "hsl(var(--sidebar-accent))",
          "accent-foreground": "hsl(var(--sidebar-accent-foreground))",
          border: "hsl(var(--sidebar-border))",
          ring: "hsl(var(--sidebar-ring))",
        },
      },
      borderRadius: {
        lg: "var(--radius)",
        md: "calc(var(--radius) - 2px)",
        sm: "calc(var(--radius) - 4px)",
      },
      keyframes: {
        "accordion-down": {
          from: {
            height: "0",
          },
          to: {
            height: "var(--radix-accordion-content-height)",
          },
        },
        "accordion-up": {
          from: {
            height: "var(--radix-accordion-content-height)",
          },
          to: {
            height: "0",
          },
        },
        "collapsible-down": {
          from: {
            height: "0",
          },
          to: {
            height: "var(--radix-collapsible-content-height)",
          },
        },
        "collapsible-up": {
          from: {
            height: "var(--radix-collapsible-content-height)",
          },
          to: {
            height: "0",
          },
        },
      },
      animation: {
        "accordion-down": "accordion-down 0.2s ease-out",
        "accordion-up": "accordion-up 0.2s ease-out",
        "collapsible-down": "collapsible-down 0.2s ease-out",
        "collapsible-up": "collapsible-up 0.2s ease-out",
      },
      typography:
        '(theme: (path: string) => string | undefined) => ({\\\\n        DEFAULT: {\\\\n          css: {\\\\n            color: theme("colors.foreground"),\\\\n            a: {\\\\n              color: theme("colors.primary.DEFAULT"),\\\\n              "&:hover": {\\\\n                color: theme("colors.primary.DEFAULT"),\\\\n              },\\\\n            },\\\\n            table: {\\\\n              width: "100%",\\\\n              borderCollapse: "collapse",\\\\n              thead: {\\\\n                borderBottom: `1px solid ${theme("colors.border")}`,\\\\n              },\\\\n              th: {\\\\n                color: theme("colors.foreground"),\\\\n                fontWeight: "600",\\\\n                padding: "0.5rem",\\\\n              },\\\\n              td: {\\\\n                color: theme("colors.foreground"),\\\\n                padding: "0.5rem",\\\\n              },\\\\n            },\\\\n            ".table-consistent-width table": {\\\\n              tableLayout: "fixed",\\\\n              width: "100%",\\\\n            },\\\\n            ".table-consistent-width th, .table-consistent-width td": {\\\\n              width: "33.33%",\\\\n              textAlign: "left",\\\\n              padding: "8px",\\\\n            },\\\\n          },\\\\n        },\\\\n        dark: {\\\\n          css: {\\\\n            color: theme("colors.foreground"),\\\\n            a: {\\\\n              color: theme("colors.primary.DEFAULT"),\\\\n              "&:hover": {\\\\n                color: theme("colors.primary.foreground"),\\\\n              },\\\\n            },\\\\n            table: {\\\\n              thead: {\\\\n                borderBottom: `1px solid ${theme("colors.border")}`,\\\\n              },\\\\n              th: {\\\\n                color: theme("colors.foreground"),\\\\n                fontWeight: "600",\\\\n              },\\\\n              td: {\\\\n                color: theme("colors.foreground"),\\\\n              },\\\\n            },\\\\n          },\\\\n        },\\\\n      })',
    },
  },
  corePlugins: {
    preflight: true,
  },
  plugins: [require("tailwindcss-animate"), require("@tailwindcss/typography")],
} satisfies Config;

export default config;
