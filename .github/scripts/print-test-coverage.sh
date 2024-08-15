#!/bin/bash

echo "### JaCoCo Test Coverage Summary"                                        >> $GITHUB_STEP_SUMMARY
echo "|     Type      |                      Coverage                       |" >> $GITHUB_STEP_SUMMARY
echo "|---------------|-----------------------------------------------------|" >> $GITHUB_STEP_SUMMARY
echo "| Overall files | ${{ steps.jacoco.outputs.coverage-overall }}%       |" >> $GITHUB_STEP_SUMMARY
echo "| Changed files | ${{ steps.jacoco.outputs.coverage-changed-files }}% |" >> $GITHUB_STEP_SUMMARY
echo "---"                                                                     >> $GITHUB_STEP_SUMMARY
