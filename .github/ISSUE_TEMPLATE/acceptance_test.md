---
name: Acceptance Test
about: List steps for a user story which the stakeholder approves.
title: 'AT-<issue#>: <description> (US-<user_story#>)'
labels: acceptance test
assignees: ''

---

### Linked User Story
<user stories related to this acceptance test>
<Example: #1, #2, #3 >

### Preconditions:
<List of conditions that must be true prior to the execution of this operation>

### Postconditions:
<List of conditions that must be true after the execution of this operation>

### Test Steps:
<step by step explanation of the operation. Example below.>

1. User enters the login page
2. User fills in credential in login form
3. User clicks login button
4. User logs into the home page
...

### Expected Result:
<List of expectations hen the steps are completed.  Example below.>

- User is logged in into the system

### Alternative Steps:
<step by step explanation of the operation covering alternate flows. Example below.>

Path 1: User failed to enter correct passwords
2. User fills in credential in login form, with wrong password
3. User clicks login button
4. Toast appears saying "Wrong Password entered"

Path 2: User didn't fill any credentials
2. User click login button without filling any information
3. Toast appears saying "No credentials entered"