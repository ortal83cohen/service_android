# Android App Structure

### Top Folders

* features - contains ui activities, fragments or jobs that stand on their own and are not reusable.
* common - contains reusable logic (see drill down).
* infra - contains low level setup, services and base classes.
* serverModel - contains server side data classes.

##### Dependencies

* Features: Can depend on common, infra and serverModel. Folder 'A' in features cannot depend on folder 'B' in features.
* Common: Can depend on infra and serverModel.
* infra: Cannot depend on anything.
* serverModel: Cannot depend on anything.

##### Commons folder structure

* dataClients: Contains DB and REST clients for the data types in the app.
* services: Contains dagger reusable services.
* utils: Contains reusable public static classes.
* uiComponents: Contains reusable ui components.