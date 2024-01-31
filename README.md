** Online Compilation&Build System **
Online project that provide a compilation/build system provided with an appropriate web API. 
The Web API and server-side implementation will incorporate Web API design patterns and classic Gang of Four (GoF) design patterns
a. GoF Design Patterns:
  o Singleton: Implement a central build queue or manager as a singleton pattern 
    to ensure that only one build is processed at a time.
    o Factory Method: Use this pattern as appropriate for creating objects in your 
    system.
  o Observer: Implement a notification system using this pattern to trigger 
    notifications and appropriate responses to build completion events.
    o Decorator pattern: Use this pattern to add additional features or 
    configurationsto a project build step (e.g. archiving some output files, setting 
    the optimization level to high…).
  o Adapter pattern: Use this pattern to provide the system with the capability to 
    handle different external build/compile systems.
b. Web API Design Patterns:
  o Resource oriented API: The API should be resource-oriented.
  o Standard Methods: Implement CRUD operations for relevant resources.
  o Long Running Operations: Apply asynchronous processing to handle 
    lengthy project builds.
  o Field Masks: Allow clients to specify which build result information they 
  want to retrieve.
