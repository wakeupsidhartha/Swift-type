// DOM Elements
const textDisplay = document.getElementById('text-display');
const textInput = document.getElementById('text-input');
const wpmValue = document.getElementById('wpm-value');
const accuracyValue = document.getElementById('accuracy-value');
const errorsValue = document.getElementById('errors-value');
const timeValue = document.getElementById('time-value');
const lessonButtons = document.querySelectorAll('.lesson-buttons button');
const resetButton = document.getElementById('reset-button');
const messageBox = document.getElementById('message-box');

// Lesson Texts
const lessons = [
    {
        name: "Home Row (asdf jkl;)",
        texts: [
            "asdf jkl; asdf jkl; asdf jkl;",
            "fjdksla fjdksla fjdksla",
            "asdfg hjkl; asdfg hjkl;",
            "a;sldkfj a;sldkfj a;sldkfj"
        ]
    },
    {
        name: "Top Row (qwerty uiop)",
        texts: [
            "qwerty uiop qwerty uiop",
            "poiuyt poiuyt poiuyt",
            "qwe rty uio p",
            "tyu iop qwe r"
        ]
    },
    {
        name: "Bottom Row (zxcvbnm,./)",
        texts: [
            "zxcvbnm,./ zxcvbnm,./",
            ".,mnbvcxz .,mnbvcxz",
            "zxc vbn m,.",
            "bnm zxc v"
        ]
    },
    {
        name: "All Letters (a-z)",
        texts: [
            "the quick brown fox jumps over the lazy dog",
            "pack my box with five dozen liquor jugs",
            "how vexingly quick daft zebras jump",
            "sphinx of black quartz judge my vow"
        ]
    },
    {
        name: "Common Words",
        texts: [
            "the and a to in is it you that he was for on are with as his they at be this have from or one had by words but not what all were we when your can said there use an each which she do how their if will up other about out many then them these so some her would make like him into time has look two more write go see number no way could people my than first water been call who oil its now find long down day did get come made may part",
            "word sentence paragraph language keyboard practice speed accuracy skill learn improve master typing computer online website lesson tutorial help guide step by step progress challenge test score result feedback correct incorrect error time minute second character letter home row top bottom all letters common words sentences"
        ]
    },
    {
        name: "Sentences",
        texts: [
            "The quick brown fox jumps over the lazy dog.",
            "Never underestimate the power of a good book.",
            "Practice makes perfect, so keep typing every day.",
            "The early bird catches the worm, but the second mouse gets the cheese.",
            "Technology has revolutionized the way we communicate and learn.",
            "The sun always shines brightest after the rain.",
            "Learning to type efficiently is a valuable skill in the digital age."
        ]
    }
];

// Global State Variables
let currentLessonIndex = 0;
let currentText = '';
let currentTextIndex = 0; // Index of the character to be typed next in currentText
let startTime = 0;
let errors = 0;
let typedCharacters = 0; // Total characters currently in the input field that match the target text length
let isStarted = false;
let timerInterval = null;

/**
 * Initializes or resets the typing test.
 * @param {number} lessonId - The index of the lesson to load.
 */
function initializeTest(lessonId) {
    currentLessonIndex = lessonId;
    currentTextIndex = 0;
    errors = 0;
    typedCharacters = 0;
    isStarted = false;
    startTime = 0;
    clearInterval(timerInterval);
    timerInterval = null;
    textInput.value = '';
    textInput.disabled = false;
    textInput.focus();
    hideMessage(); // Hide any previous messages

    // Select a random text from the current lesson
    const lessonTexts = lessons[currentLessonIndex].texts;
    currentText = lessonTexts[Math.floor(Math.random() * lessonTexts.length)];

    renderText();
    updateStats();
    updateLessonButtons();
}

/**
 * Renders the current text in the display area, highlighting typed and current characters.
 */
function renderText() {
    textDisplay.innerHTML = '';
    const inputValue = textInput.value; // Get the current value from the input field

    for (let i = 0; i < currentText.length; i++) {
        const charSpan = document.createElement('span');
        charSpan.textContent = currentText[i];

        if (i < inputValue.length) { // Compare against the actual input value length
            // Character has been typed
            if (inputValue[i] === currentText[i]) {
                charSpan.classList.add('correct');
            } else {
                charSpan.classList.add('incorrect');
            }
        } else if (i === inputValue.length) { // Current character to be typed
            charSpan.classList.add('current');
        }
        textDisplay.appendChild(charSpan);
    }
}

/**
 * Updates the WPM, Accuracy, and Errors displayed on the screen.
 */
function updateStats() {
    let wpm = 0;
    let accuracy = 0;
    let timeElapsed = 0;

    if (isStarted && startTime > 0) {
        timeElapsed = Math.floor((Date.now() - startTime) / 1000); // in seconds
        timeValue.textContent = `${timeElapsed}s`;

        if (timeElapsed > 0) {
            // WPM = (Number of correct characters currently in input / 5) / (Time in minutes)
            // This calculates "net WPM" based on the current state of the input.
            const netTypedCharacters = typedCharacters - errors;
            wpm = Math.round((netTypedCharacters / 5) / (timeElapsed / 60));
            wpm = Math.max(0, wpm); // Ensure WPM is not negative
        }
    } else {
        timeValue.textContent = '0s';
    }

    if (typedCharacters > 0) {
        accuracy = Math.round(((typedCharacters - errors) / typedCharacters) * 100);
    }

    wpmValue.textContent = wpm;
    accuracyValue.textContent = `${accuracy}%`;
    errorsValue.textContent = errors;
}

/**
 * Displays a message to the user.
 * @param {string} message - The message text.
 * @param {string} type - 'success' or 'error' for styling.
 */
function showMessage(message, type) {
    messageBox.textContent = message;
    messageBox.className = `message-box ${type}`; // Reset classes and add new ones
    messageBox.classList.remove('hidden');
}

/**
 * Hides the message box.
 */
function hideMessage() {
    messageBox.classList.add('hidden');
}

/**
 * Handles user input from the text input field.
 * This function is triggered by the 'input' event, which fires for any change to the input's value,
 * including typing new characters, deleting with backspace, pasting, etc.
 */
function handleInput() {
    const inputValue = textInput.value;

    // Start timer on first character typed
    if (!isStarted && inputValue.length > 0) {
        isStarted = true;
        startTime = Date.now();
        timerInterval = setInterval(updateStats, 1000); // Update time every second
    } else if (isStarted && inputValue.length === 0) {
        // If all text is deleted after starting, stop timer and reset stats
        clearInterval(timerInterval);
        timerInterval = null;
        isStarted = false;
        startTime = 0;
        errors = 0;
        typedCharacters = 0;
    }

    // Update currentTextIndex based on the actual input length
    currentTextIndex = inputValue.length;

    // Recalculate errors and typedCharacters based on current input value
    errors = 0;
    typedCharacters = inputValue.length; // 'typedCharacters' now reflects the current length of the input field

    // Calculate errors by comparing typed input with the target text
    const compareLength = Math.min(inputValue.length, currentText.length);
    for (let i = 0; i < compareLength; i++) {
        if (inputValue[i] !== currentText[i]) {
            errors++;
        }
    }
    // Add errors for any extra characters typed beyond the currentText length
    errors += Math.max(0, inputValue.length - currentText.length);


    // Update UI
    renderText();
    updateStats();

    // Check if text is completed or if corrections are needed
    if (inputValue === currentText) {
        // Test completed successfully (input exactly matches target text)
        clearInterval(timerInterval);
        isStarted = false;
        textInput.disabled = true;
        showMessage('Test Completed! Press Reset to try again.', 'success');
    } else if (inputValue.length >= currentText.length && errors > 0) {
        // If typed the full length (or more) but there are errors, prompt for correction
        showMessage('Keep going! Correct the errors to finish.', 'error');
    } else {
        // Hide message if still typing or correcting errors
        hideMessage();
    }
}

/**
 * Updates the active state of lesson buttons.
 */
function updateLessonButtons() {
    lessonButtons.forEach(button => {
        if (parseInt(button.dataset.lessonId) === currentLessonIndex) {
            button.classList.add('active');
        } else {
            button.classList.remove('active');
        }
    });
}

// Event Listeners
textInput.addEventListener('input', handleInput); // 'input' event captures all changes including backspace

lessonButtons.forEach(button => {
    button.addEventListener('click', () => {
        const lessonId = parseInt(button.dataset.lessonId);
        initializeTest(lessonId);
    });
});

resetButton.addEventListener('click', () => {
    initializeTest(currentLessonIndex); // Reset to the current lesson
});

// Initial setup
initializeTest(0); // Load the first lesson by default
