# Eightfold AI-SDET Interview Prep Plan

## Context

This is a 90-minute technical round for an AI-SDET Engineer role at Eightfold. The interview has two distinct parts:
1. **Live coding** — medium-to-hard problem in a browser-based editor with an AI coding assistant. They're explicitly watching *how* you collaborate with AI, not just whether you solve it.
2. **AI system design discussion** — designing and validating an AI-powered test system for a realistic enterprise scenario.

The three things they're grading on:
- Writing correct, well-structured code
- Using AI tools with intent — directing the AI, catching weak output, not blindly accepting suggestions
- QA systems thinking: determinism, failure handling, observability, and whether a test result can actually be trusted

---

## Folder Structure

```
/coding-practice      → DSA problems, solutions, test cases
/test-design-notes    → AI test design scenarios and discussion notes
```

---

## Prep Sessions (Recommended Order)

### Session 1 — Coding Warm-Up + AI Collaboration (Today)
**Goal:** Get in the rhythm of solving problems *with* an AI assistant, practicing how to guide it and catch its mistakes.

1. Solve the first problem in `/coding-practice/problem_01_starter.py`
2. Claude will act as your AI pair programmer:
   - Suggest an approach with narrated reasoning
   - Deliberately include minor edge-case gaps or sub-optimal choices
   - You catch, question, and correct
3. After solving, write test cases together and run them

**First problem:** Sliding window / string manipulation — high-signal for medium-hard interviews, maps to real QA work.

---

### Session 2 — Coding: Arrays + Two Pointers
**Goal:** Cover the second most common pattern at this level.

Problems:
- Two-sum variant with constraints (sorted array, unique pairs, k-diff)
- Merge intervals / overlapping windows
- Container with most water

Practice the same AI collaboration loop: get a suggestion → probe it → find the gap → fix it.

---

### Session 3 — Coding: Graphs + BFS/DFS
**Goal:** Cover tree/graph traversal — likely to appear as a "harder" problem if the first one goes fast.

Problems:
- Number of islands (BFS)
- Course schedule / cycle detection (DFS)
- Word ladder (BFS with deduplication)

---

### Session 4 — Python Testing Tooling Review
**Goal:** Be fluent with pytest, requests/httpx, and mocking patterns — the tools they expect you to use.

Topics:
- pytest fixtures, parametrize, conftest.py patterns
- httpx for async API testing
- How to mock LLM responses deterministically in tests
- Writing idempotent, isolated test cases

---

### Session 5 — AI Test Design Discussion Practice
**Goal:** Be able to talk through designing AI-powered test systems for 30 minutes, fluently.

Scenario topics (in `/test-design-notes/scenario_questions.md`):
1. Testing an AI feature that classifies job applicant resumes — how do you validate?
2. Designing a test suite where the "oracle" is another AI model — how do you trust the result?
3. How do you make LLM-based test assertions reproducible across runs?
4. Failure handling: what happens when the AI component returns garbage, times out, or hallucinates?
5. Observability: how do you instrument an AI-driven test pipeline so failures are debuggable?
6. Multi-tenant / role-based: an AI feature returns different output per tenant — how do you test this at scale?

---

## AI Collaboration Rules (for Practice)

These mirror what the interviewers will watch for:

| What they want to see | How to practice it |
|---|---|
| Give the AI clear, specific prompts | Start every problem by describing constraints before asking for code |
| Recognize weak output | When Claude suggests something, ask "what edge case does this miss?" |
| Correct without starting over | Patch the AI's suggestion rather than rewriting from scratch |
| Don't blindly accept passing tests | Run the tests AND question whether they cover the real cases |

---

## Key Topics to Have Ready (Talk-Track)

For the design discussion, be ready to speak to these without hesitation:

**Determinism**
- LLMs are non-deterministic by default; use `temperature=0` + seed where supported
- Cache responses for test repeatability; version-lock the model

**Failure Handling**
- Distinguish between AI errors (hallucination, refusal) and infrastructure errors (timeout, rate limit)
- Design fallback assertions that don't depend on the AI path

**Observability**
- Log prompts, responses, latency, and token counts per test run
- Use trace IDs to correlate AI calls back to specific test cases

**Trustworthiness of AI-generated tests**
- An AI-generated test that always passes is not a test — it may be testing the wrong thing
- Use mutation testing or inject known bugs to verify the test can fail
- Human review checkpoint for AI-generated test assertions

**Scalability**
- Cost control: cache LLM calls, use smaller models for simple assertions
- Parallelism: LLM calls are slow; async test execution with concurrency limits
- Flakiness: quarantine tests that rely on non-deterministic AI output

---

## Success Criteria

- Can solve a medium problem in ~25 minutes with an AI assistant
- Can identify and correct at least one AI-introduced flaw per problem
- Can speak for 5+ minutes on any of the 6 test design scenarios without notes
- Comfortable in Python with pytest + httpx patterns
