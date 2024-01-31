# Online Compilation&Build System
<p> Online project that provide a compilation/build system provided with an appropriate web API.</p> 
<p>The Web API and server-side implementation will incorporate Web API design patterns and classic Gang of Four (GoF) design patterns</p>
GoF Design Patterns:

  - Singleton: Implement a central build queue or manager as a singleton pattern 
    to ensure that only one build is processed at a time.
    
  - Factory Method: Use this pattern as appropriate for creating objects in your 
    system.
    
  - Observer: Implement a notification system using this pattern to trigger 
    notifications and appropriate responses to build completion events.
    
  - Decorator pattern: Use this pattern to add additional features or 
    configurationsto a project build step (e.g. archiving some output files, setting 
    the optimization level to high…).
    
  - Adapter pattern: Use this pattern to provide the system with the capability to 
    handle different external build/compile systems.
    
Web API Design Patterns:

  - Resource oriented API: The API should be resource-oriented.
    
  - Standard Methods: Implement CRUD operations for relevant resources.
    
  - Long Running Operations: Apply asynchronous processing to handle
    lengthy project builds.
    
  - Field Masks: Allow clients to specify which build result information they 
  want to retrieve.
