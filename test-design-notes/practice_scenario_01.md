# Practice Scenario 01: AI Candidate-Job Match Scorer

## The Scenario (Interviewer Prompt)

> "Eightfold's platform uses an AI model to score how well a candidate matches
> a job opening. Given a candidate's resume and a job description, the model
> returns a match score (0–100) and a list of matched and missing skills.
> This score is used to rank candidates in the recruiter's pipeline.
>
> You're the AI-SDET responsible for validating this feature before it ships.
> Walk me through how you would design and validate the test system for it."

---

## What the Interviewer Is Probing For

(Do not read this before you answer — use it for debrief afterward)

1. **Trustworthiness** — How do you know the score is meaningful, not just that the model ran?
2. **Non-determinism** — The same resume + JD might score differently on two runs. How do you handle this in tests?
3. **Failure recovery** — What happens if the model returns null, an out-of-range score, or times out?
4. **Observability** — If a recruiter reports "this candidate shouldn't have ranked #1", how do you debug it?
5. **Regression** — When the model is retrained, how do you know the scores didn't silently degrade?
6. **Fairness / bias** — Bonus: does your test plan catch if the model scores identical resumes differently based on name?

---

## Follow-up Questions (Interviewer will pick from these based on your answer)

- "You said you'd compare scores to a golden dataset — who creates that dataset, and how do you keep it from going stale?"
- "Your test passes because the score is between 0 and 100. Is that actually a useful test?"
- "The model was updated last week and scores shifted by ~5 points across the board. Is that a bug or expected behavior?"
- "A recruiter says the AI ranked a clearly underqualified candidate in the top 3. How do you reproduce and debug that?"
- "How do you make sure your tests aren't just validating that the model is consistent with itself?"

---

## Space for Your Answer

(Type your response here during practice, then review the debrief section above)
