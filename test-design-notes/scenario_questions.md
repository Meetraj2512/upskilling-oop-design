# AI Test Design — Scenario Practice Questions

Practice talking through each of these out loud for 5+ minutes without notes.
The goal isn't to recite a perfect answer — it's to reason through the problem
the way a senior SDET would in a live discussion.

---

## Scenario 1: Testing an AI Resume Classifier

> Eightfold's core product classifies job applicants by parsing resumes and
> inferring skills, seniority, and fit. How would you design a test suite for
> this feature?

**Things to cover:**
- What is the "ground truth" — who labels the expected output, and how do you handle disagreement?
- How do you test at the boundary (e.g., a candidate who is *almost* senior)?
- How do you detect regression when the model is retrained or updated?
- What does a failing test mean here — wrong label, low confidence, or something else?
- How do you handle inputs the model has never seen (novel formats, non-English resumes)?

**Probing questions the interviewer might ask:**
- "How do you avoid the test suite just memorizing the model's current behavior?"
- "What's your strategy when ground truth is ambiguous?"

---

## Scenario 2: AI-as-Oracle — Can You Trust the Judge?

> You're testing an AI feature and you decide to use another AI model to
> evaluate whether the output is correct. How do you validate that the
> judging model is trustworthy?

**Things to cover:**
- The circular dependency problem: if the judge is wrong, your tests are wrong
- Ways to validate the judge: agreement with human labels on a sample set, inter-rater reliability
- When to use AI-as-oracle vs. deterministic assertions
- How to version and pin the judge model so test results are reproducible
- Cost of using a second model call per test — when is it worth it?

**Probing questions the interviewer might ask:**
- "Your judge model was updated last week. How would you know if your tests silently became unreliable?"
- "What's an example where a deterministic assertion is strictly better than an AI judge?"

---

## Scenario 3: Reproducibility of LLM-Based Test Assertions

> You have a test that calls an LLM and asserts something about the response.
> It passed on Tuesday and failed on Thursday with no code change. What
> happened, and how do you prevent it?

**Things to cover:**
- Sources of non-determinism: temperature > 0, no seed, model updates, prompt caching behavior
- Mitigation: `temperature=0`, fixed seed, response caching (record/replay), version-locking the model
- The distinction between a flaky test and a real regression
- When to use fuzzy matching vs. exact matching in assertions
- How to quarantine known-flaky AI tests without silently ignoring real failures

**Probing questions the interviewer might ask:**
- "If you cache LLM responses for test replay, what happens when the underlying feature changes?"
- "How do you document that a test is intentionally non-deterministic vs. unintentionally flaky?"

---

## Scenario 4: Failure Handling in AI-Driven Test Pipelines

> Your test suite includes steps that call an external LLM API. During a CI
> run, the API times out. How should your test framework handle this, and
> how do you differentiate infrastructure failures from real test failures?

**Things to cover:**
- Categories of failure: infrastructure (timeout, rate limit, 503), model (hallucination, refusal, empty response), assertion (output doesn't match expectation)
- Each failure type should produce a different test outcome: `ERROR` vs `FAIL` vs `XFAIL`
- Retry strategy: when to retry (timeouts) vs. when not to (bad output)
- Fallback paths: can the test assert something without the AI response?
- Alerting: how do you know if your AI test infrastructure is degraded vs. your product is broken?

**Probing questions the interviewer might ask:**
- "Your test marked a run as PASS because it hit the LLM fallback path. Is that actually a passing test?"
- "How do you set rate limit budgets for LLM calls in a large CI pipeline?"

---

## Scenario 5: Observability — Making AI Test Failures Debuggable

> A test in your AI-driven pipeline fails overnight. By the time an engineer
> looks at it in the morning, the exact conditions that caused the failure
> may be gone. How do you instrument your pipeline to make this debuggable?

**Things to cover:**
- What to log per test run: prompt sent, raw response, parsed output, assertion result, latency, token count, model version
- Trace IDs: correlate a test failure back to the exact LLM call
- Structured logging vs. plain text — why structured matters for downstream analysis
- Storing artifacts: save the full prompt/response pair, not just a summary
- Retention policy: how long do you keep LLM call logs? Cost vs. debuggability tradeoff

**Probing questions the interviewer might ask:**
- "Your logs show the LLM returned a valid-looking response, but the test still failed. What do you look at next?"
- "How do you avoid logging sensitive PII that might appear in resumes passed to the model?"

---

## Scenario 6: Multi-Tenant / Role-Based AI Testing at Scale

> Eightfold serves hundreds of enterprise customers. The AI ranking feature
> may be configured differently per tenant (different weights, models, or
> prompts). How do you test this at scale without running the full suite
> for every tenant?

**Things to cover:**
- Tenant-specific configuration as a test dimension — how do you parameterize it?
- Risk-based sampling: you can't test every tenant × every scenario; how do you prioritize?
- Isolating tenant data: tests must not cross tenant boundaries or leak data
- Canary tenants: designate a small set of representative tenants for full regression
- Detecting when a tenant config change breaks something only for that tenant

**Probing questions the interviewer might ask:**
- "A tenant customizes their ranking prompt. Who is responsible for testing that the customization works?"
- "How do you ensure a test for Tenant A doesn't accidentally read data from Tenant B?"

---

## Quick Reference: Core Concepts to Weave In

| Concept | One-liner to drop naturally |
|---|---|
| Determinism | "I'd pin `temperature=0` and version-lock the model to make this repeatable" |
| Failure categories | "That's an infrastructure error, not a product failure — it should be `ERROR` not `FAIL`" |
| Observability | "I'd log the full prompt and response with a trace ID so we can replay the failure" |
| Trustworthiness | "A test that always passes isn't a test — I'd inject a known bad input to verify it can fail" |
| Cost control | "I'd cache LLM responses across test runs and use a smaller model for simple format assertions" |
| Flakiness | "I'd quarantine it with `@pytest.mark.flaky` and track failure rate before deleting it" |
