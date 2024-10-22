# neXtgen Project Setup Guide 🚀

Thank you for contributing to the neXtgen project! 🎉 Follow these steps to set up the project on your local machine:

## 1. Clone Your Repository
Open your terminal and run:
```bash
git clone https://github.com/your-username/NextGen.git
```

## 2. Open in Android Studio
- Launch Android Studio.
- Select **File > Open** and navigate to the cloned `NextGen` directory.

## 3. Project Structure 📂
- **`app/`**: Main application code
- **`lib/`**: Reusable libraries
- **`docs/`**: Documentation

## 4. Show Files
In the left project view, select **Project** to see all files.

## 5. Create `secrets.properties`
In the `NextGen` directory, create a file named `secrets.properties`.

## 6. Add Your Keys 🔑
Open `secrets.properties` and add:
```properties
MAPS_API_KEY=[your google maps api key]
SERVER_URL=[your video call server URL]
```

## 7. Clean & Rebuild 🛠️
- Go to **Build > Clean Project**.
- Then select **Build > Rebuild Project**.

## 8. Good Coding Practices 📝
- Write clean, readable code.
- Use meaningful names and comment where needed.


<br>

# Need help getting started?? 🤔

If you're new to Git and GitHub, no worries! Here are some useful resources:

- [Forking a Repository](https://help.github.com/en/github/getting-started-with-github/fork-a-repo)
- [Cloning a Repository](https://help.github.com/en/desktop/contributing-to-projects/creating-an-issue-or-pull-request)
- [How to Create a Pull Request](https://opensource.com/article/19/7/create-pull-request-github)
- [Getting Started with Git and GitHub](https://towardsdatascience.com/getting-started-with-git-and-github-6fcd0f2d4ac6)
- [Learn GitHub from Scratch](https://docs.github.com/en/get-started/start-your-journey/git-and-github-learning-resources)

<br>

# Project Structure 📂

```bash
NextGen/
├── .github/                  # Configuration files for GitHub, including issue templates
├── .idea/                    # IDE-specific files for Android Studio
├── app/                      # Main application code
│   ├── src/                  # Source code for the app
│   ├── res/                  # Resources like layouts, drawables, etc.
│   ├── build.gradle          # Gradle build configuration for the app module
│   └── fragment_profile.xml   # XML layout for the profile fragment
├── data/                     # Data handling, including repositories and models
│   └── model/                # Data models used in the application
├── domain/                   # Domain logic and business rules
├── gradle/                   # Gradle-related files
│   ├── wrapper/              # Wrapper files for Gradle
│   └── build.gradle          # Project-level Gradle configuration
├── gradlew                   # Unix executable for Gradle
├── gradlew.bat               # Windows executable for Gradle
├── local.defaults.properties  # Local default properties for configuration
├── settings.gradle           # Project settings for Gradle
├── .gitignore                # Git ignore file for untracked files
├── CONTRIBUTORS.md           # List of contributors to the project
├── CODE_OF_CONDUCT.md        # Code of conduct for contributors
├── LICENSE                   # License file for the project
├── README.md                 # Main README file with project details
└── PULL_REQUEST_TEMPLATE.md   # Template for pull requests

```

<br>


# Alternatively contribute using GitHub Desktop 🖥️

1. **Open GitHub Desktop:**
   Launch GitHub Desktop and log in to your GitHub account if you haven't already.

2. **Clone the Repository:**
   - If you haven't cloned the repository yet, you can do so by clicking on the "File" menu and selecting "Clone Repository."
   - Choose the repository from the list of repositories on GitHub and clone it to your local machine.

3. **Switch to the Correct Branch:**
   - Ensure you are on the branch that you want to submit a pull request for.
   - If you need to switch branches, you can do so by clicking on the "Current Branch" dropdown menu and selecting the desired branch.

4. **Make Changes:**
   Make your changes to the code or files in the repository using your preferred code editor.

5. **Commit Changes:**
   - In GitHub Desktop, you'll see a list of the files you've changed. Check the box next to each file you want to include in the commit.
   - Enter a summary and description for your changes in the "Summary" and "Description" fields, respectively. Click the "Commit to <branch-name>" button to commit your changes to the local branch.

6. **Push Changes to GitHub:**
   After committing your changes, click the "Push origin" button in the top right corner of GitHub Desktop to push your changes to your forked repository on GitHub.

7. **Create a Pull Request:**
   - Go to the GitHub website and navigate to your fork of the repository.
   - You should see a button to "Compare & pull request" between your fork and the original repository. Click on it.

8. **Review and Submit:**
   - On the pull request page, review your changes and add any additional information, such as a title and description, that you want to include with your pull request.
   - Once you're satisfied, click the "Create pull request" button to submit your pull request.

9. **Wait for Review:**
    Your pull request will now be available for review by the project maintainers. They may provide feedback or ask for changes before merging your pull request into the main branch of the repository.

<br>



# Pull Request Process 🚀

When submitting a pull request, please adhere to the following:

1. **Self-review your code** before submission. 
2. Include a detailed description of the functionality you’ve added or modified.
3. Comment your code, especially in complex sections, to aid understanding.
4. Add relevant screenshots to assist in the review process.
5. Submit your PR using the provided template and hang tight; we'll review it as soon as possible! 🚀

<br>

# Issue Report Process 📌

To report an issue, follow these steps:

1. Navigate to the project's issues section :- [Issues](https://github.com/Vishwajith-Shettigar/NextGen/issues)
2. Provide a clear and concise description of the issue.
3. **Avoid spamming** to claim an issue. Patience is key! 
4. Wait until someone looks into your report.
5. Begin working on the issue only after you have been assigned to it. 🚀

<br>

# Thank you for contributing 💗

We truly appreciate your time and effort to help improve our project. Feel free to reach out if you have any questions or need guidance. Happy coding! 🚀

##
