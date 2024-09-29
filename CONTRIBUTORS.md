
## Setup Instructions for neXtgen

Follow these steps to set up the neXtgen project on your local machine:

1. **Clone Your Forked Repository**
   - Open your terminal and run the following command to clone your forked repository:
     ```bash
     git clone https://github.com/your-username/NextGen.git
     ```

2. **Open the Project in Android Studio**
   - Launch Android Studio.
   - Select **File > Open** and navigate to the cloned `NextGen` project directory.

3. **Select "Show Files" in Project View**
   - In the project view on the left side, select **Project** from the dropdown to see all files.

4. **Create a File Named `secrets.properties`**
   - In the main project directory (which is `NextGen`), create a new file named `secrets.properties`.

5. **Add the Following to `secrets.properties`**
   - Open the `secrets.properties` file and add the following lines, replacing the placeholders with your actual values:
     ```properties
     MAPS_API_KEY=[your google maps api key (no need to quote it with double quotes)]
     SERVER_URL=[your video call server URL]
     ```

6. **Clean and Rebuild the Project**
   - In Android Studio, go to **Build > Clean Project**.
   - After that, select **Build > Rebuild Project** to ensure everything is set up correctly.

7. **Notes**
   - Make sure to add `secrets.properties` to your `.gitignore` file to prevent it from being tracked by Git.
   - Keep your API keys and sensitive information secure and do not share them publicly.

---

Thank you for contributing to the neXtgen project! 🎉
