from pydantic import BaseModel


class ApplicationRequest(BaseModel):
    name: str
    email: str
    resume_text: str
    job_id: str


class ApplicationResponse(BaseModel):
    success: bool
    message: str
