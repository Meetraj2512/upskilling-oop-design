from fastapi import FastAPI
from models import ApplicationRequest, ApplicationResponse
from handler import handle_submission

app = FastAPI()


@app.post("/apply", response_model=ApplicationResponse)
async def submit_application(payload: ApplicationRequest):
    return handle_submission(payload)
