FROM node:20-alpine AS base

# Install dependencies only when needed
FROM base AS deps
# Check https://github.com/nodejs/docker-node/tree/b4117f9333da4138b03a546ec926ef50a31506c3#nodealpine to understand why libc6-compat might be needed.
RUN apk add --no-cache libc6-compat
RUN npm install -g pnpm
WORKDIR /app

# Copy the entire repo with all workspaces
COPY . .

# Check if standalone mode is enabled in next.config.mjs
RUN cd apps/frontend && \
    if grep -q "output: 'standalone'" next.config.mjs || grep -q 'output: "standalone"' next.config.mjs; then \
        echo "Standalone mode is already configured"; \
    else \
        echo "Updating next.config.mjs to include standalone mode"; \
        sed -i '/const nextConfig/a \ \ output: "standalone",' next.config.mjs; \
    fi

# Install all dependencies for the entire workspace, but skip postinstall scripts
ENV SKIP_POSTINSTALL=1
RUN pnpm install --frozen-lockfile

# Rebuild the source code only when needed
FROM base AS builder
WORKDIR /app
# Install pnpm in the builder stage too
RUN npm install -g pnpm
COPY --from=deps /app ./

# Next.js collects completely anonymous telemetry data about general usage.
ENV NEXT_TELEMETRY_DISABLED=1

# Build the frontend app
RUN cd apps/frontend && pnpm run build

# Production image, copy all the files and run next
FROM base AS runner
WORKDIR /app

ENV NODE_ENV=production
ENV NEXT_TELEMETRY_DISABLED=1

RUN addgroup --system --gid 1001 nodejs
RUN adduser --system --uid 1001 nextjs

# Copy the standalone output
COPY --from=builder /app/apps/frontend/.next/standalone/ ./

# Copy public and static files
COPY --from=builder /app/apps/frontend/public ./apps/frontend/public
COPY --from=builder /app/apps/frontend/.next/static ./apps/frontend/.next/static

# Set proper permissions
RUN chown -R nextjs:nodejs ./apps/frontend/.next

USER nextjs

EXPOSE 3000

ENV PORT=3000
ENV HOSTNAME="0.0.0.0"

# Run the server
CMD ["node", "apps/frontend/server.js"]