import React from 'react';
import { CheckIcon } from '@heroicons/react/24/outline';

const Stepper = ({ steps, currentStep }) => {
  return (
    <nav aria-label="Progress">
      <ol className="flex items-center justify-center">
        {steps.map((step, stepIdx) => (
          <li key={stepIdx} className={`flex items-center ${stepIdx !== steps.length - 1 ? 'pr-8 sm:pr-20' : ''}`}>
            <div className="flex items-center">
              <div className={`flex h-10 w-10 items-center justify-center rounded-full border-2 ${
                stepIdx < currentStep
                  ? 'bg-blue-600 border-blue-600'
                  : stepIdx === currentStep
                  ? 'border-blue-600'
                  : 'border-gray-300'
              }`}>
                {stepIdx < currentStep ? (
                  <CheckIcon className="h-6 w-6 text-white" aria-hidden="true" />
                ) : (
                  <span className={`text-sm font-medium ${
                    stepIdx === currentStep ? 'text-blue-600' : 'text-gray-500'
                  }`}>
                    {stepIdx + 1}
                  </span>
                )}
              </div>
              <span className={`ml-4 text-sm font-medium ${
                stepIdx === currentStep ? 'text-blue-600' : 'text-gray-500'
              }`}>
                {step}
              </span>
            </div>
            {stepIdx !== steps.length - 1 && (
              <div className={`flex-1 h-0.5 mx-4 ${
                stepIdx < currentStep ? 'bg-blue-600' : 'bg-gray-300'
              }`} />
            )}
          </li>
        ))}
      </ol>
    </nav>
  );
};

export default Stepper;
