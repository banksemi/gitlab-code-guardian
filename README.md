# GitLab Code Guardian

AI-powered automated code review assistant for GitLab merge requests using Google's Gemini AI.

## Usage Guide

### Quick Start with Docker

1. Pull the latest image:
   ```bash
   docker pull ghcr.io/banksemi/gitlab-code-guardian:latest
   ```

2. Run with environment variables:
   ```bash
   docker run -d \
     -p 8080:8080 \
     -e LLM_GOOGLE_BASE_URL=your_google_ai_url \
     -e LLM_GOOGLE_API_KEY=your_google_api_key \
     -e GITLAB_BASE_URL=your_gitlab_url \
     -e GITLAB_TOKEN=your_gitlab_token \
     -e GITLAB_WEBHOOK_SECRET=your_webhook_secret \
     -e GITLAB_BOT_ID=your_bot_id \
     -e GITLAB_REPOSITORY=your_repository \
     ghcr.io/banksemi/gitlab-code-guardian:latest
   ```

### Development Setup

1. Set up environment variables:
   ```bash
   cp .env.example .env
   ```

2. Configure your `.env` file with the following variables:
   - `LLM_GOOGLE_BASE_URL`: Google AI API base URL
   - `LLM_GOOGLE_API_KEY`: Your Google AI API key
   - `LLM_GOOGLE_MODEL`: AI model to use (default: `gemini-2.5-pro`)
   - `GITLAB_BASE_URL`: Your GitLab instance URL
   - `GITLAB_TOKEN`: GitLab personal access token with API access
   - `GITLAB_WEBHOOK_SECRET`: Secret for webhook authentication
   - `GITLAB_BOT_ID`: Bot user ID for filtering self-comments
   - `GITLAB_REPOSITORY`: Target repository identifier

3. Build and run with Docker Compose:
   ```bash
   docker-compose up --build
   ```

### GitLab Webhook Configuration

Configure your GitLab project webhook:
- URL: `http://your-server:8080/gitlab/webhook`
- Secret Token: Same as `GITLAB_WEBHOOK_SECRET`
- Trigger events: Merge request events, Comments