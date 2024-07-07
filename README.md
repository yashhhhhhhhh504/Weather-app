# Weather App

The Weather App, developed on Android Studio, offers a comprehensive solution for accessing weather information on-the-go. With real-time updates, graphical representations of past trends, and customizable settings, it provides users with accurate and intuitive weather data.

## Features

### Android Features Utilized
- **Sensor Manager (GPS/Location)**: Retrieves user's current location for accurate weather data.
- **Accessibility**: Supports Talkback for auditory weather information.
- **Database**: Stores saved cities and weather information.
- **Activities**: Utilizes multiple activities for seamless navigation.
- **API Calls**: Interacts with Open Meteo and Geocoding APIs for weather and location data.
- **Intents**: Facilitates navigation between different app functionalities.

### App Functionalities
- **Displaying Weather Information**: Temperature, humidity, wind speed, etc.
- **Current Location Weather**: Obtains and displays weather data based on the user's current location.
- **Graphical Representation**: Shows past weather trends and future forecasts through graphs.
- **Predictions and Comparisons**: Compares current weather conditions with historical data.
- **Saved Cities**: Allows saving and accessing weather information of multiple cities.
- **Configuration and Settings**: Customizable settings for temperature units, wind speed units, etc.

### Activities
1. **Main Activity**: 
   - Displays current weather information.
   - Shows past weather trends and forecasts.
   - Enables navigation to saved cities and settings.
   - Supports Talkback for auditory weather updates.

2. **Forecast Activity**:
   - Displays weather forecast for the next 7 days.

3. **Graphs Activity**:
   - Graphical representation of past and current weather data.

4. **City List Activity**:
   - Allows searching and saving weather information for multiple cities.

5. **Settings Activity**:
   - Customizable settings for user preferences.

## Backend Working
- **Location Retrieval**: Uses Sensor Manager and Geocoding API to obtain user's coordinates and location name.
- **Weather Data Retrieval**: Utilizes Open Meteo API for current weather data and forecasts.
- **Graph Generation**: Implements Retrofit for network requests and YML charts library for graphical representation.
- **Database Management**: Stores saved cities and weather information for easy access.
- **Settings Management**: Passes user preferences as intents for updating the Main Activity.

## Getting Started
1. **Installation**: Download and install Android Studio from the official website.
2. **Clone the Repository**: Clone the Weather App repository to your local machine using Git.
3. **Open in Android Studio**: Open Android Studio and select "Open an existing Android Studio project". Navigate to the cloned repository folder.
4. **Sync Gradle**: Let Gradle sync automatically or click "Sync Project with Gradle Files" from the toolbar.
3. **Permissions**: Ensure location permission is granted for accurate weather data.
4. **Usage**: Open the app to view current weather information, forecasts, and more.
5. **Customization**: Explore settings to customize units and preferences according to your liking.
