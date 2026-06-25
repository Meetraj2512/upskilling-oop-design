from models import ApplicationRequest, ApplicationResponse


def handle_submission(payload: ApplicationRequest) -> ApplicationResponse:
    # TODO:
    # 1. Validate required fields are non-empty
    # 2. Validate email format
    # 3. Check for duplicate submission (same email + job_id)
    # 4. On success, record the submission and return success response
    # 5. On any failure, log the reason and return appropriate error response
    pass
