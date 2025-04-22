---
name: "[BUG]"
about: Template to create an issue to describe a bug which needs fixing
title: "[BUG]"
labels: bug
assignees: ''
body:
  - type: markdown
    attributes: 
      value: | 
        Please fill out the sections below to describe the bug
  - type: textarea
    attribute: 
      label: Describe the bug.
    validation: 
      required: true
  - type: textarea
    id: steps
    attributes:
      label: Steps to reproduce
      placeholder: |
        1. Go to page X
        2. Click here
        3. Click there
    validations:
      required: true
  - type: textarea
    id: expected
    attributes:
      label: What was the expected result?
      placeholder: I expected this to happen
  - type: textarea
    id: expected
    attributes:
      label: What actual happened ?
      placeholder: This happened
  - type: textarea
    id: screenshots
    attributes:
      label: Put here any screenshots or videos (optional)
---


