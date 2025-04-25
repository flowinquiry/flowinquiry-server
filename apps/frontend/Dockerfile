# Base image with Node and pnpm installed
FROM node:20-alpine AS base
RUN apk add --no-cache libc6-compat
RUN npm install -g pnpm

# Dependencies layer
FROM base AS deps
WORKDIR /repo

# Copy only lockfile and workspace files to leverage caching
COPY ../../pnpm-lock.yaml ../../pnpm-workspace.yaml ./
COPY ../../package.json ./

# Copy the app's package.json to ensure correct dependencies resolution
COPY ../../apps/frontend/package.json ./apps/frontend/

# Install all workspace deps
RUN pnpm install --frozen-lockfile

# Build layer
FROM base AS builder
WORKDIR /repo

# Copy everything needed to build
COPY ../../ ./

# Build only the frontend app
WORKDIR /repo/apps/frontend
RUN pnpm --filter ./apps/frontend... run build

# Production image
FROM node:20-alpine AS runner
WORKDIR /app

ENV NODE_ENV=production
ENV PORT=3000
ENV HOSTNAME="0.0.0.0"

RUN addgroup --system --gid 1001 nodejs
RUN adduser --system --uid 1001 nextjs

# Copy standalone Next.js output
COPY --from=builder /repo/apps/frontend/public ./public
COPY --from=builder --chown=nextjs:nodejs /repo/apps/frontend/.next/standalone ./
COPY --from=builder --chown=nextjs:nodejs /repo/apps/frontend/.next/static ./.next/static

USER nextjs

EXPOSE 3000
CMD ["node", "server.js"]
