# VoxIgnota - Sign Language to Text

VoxIgnota is an Android application that translates American Sign Language (ASL) gestures into text in real-time using the device's camera. It leverages on-device machine learning to provide a seamless translation experience.

> [!NOTE]
> Not a complete app yet, but yes it works.  
> Sign detection is too fast [I mean very fast really,not usable] but still it works.

## 🚀 Features

*   **Real-time Sign to Text:** Translates ASL signs captured from the camera into text instantly.
*   **Camera Switching:** Easily switch between front and back cameras.
*   **History:** Save the translated text for future reference.
*   **On-Device Inference:** All processing happens locally on the device, ensuring privacy and offline functionality.

## 📖 How It Works

The application uses the **CameraX** library to access the camera feed. Each frame is preprocessed (resized, converted to grayscale) and fed into a **TensorFlow Lite** model (`asl_model.tflite`). The model predicts the ASL sign, and the corresponding character is displayed on the screen. The recognized text can be saved into a local **Room database**.

## 🛠️ Technologies Used

*   **Android SDK (Java)**
*   **CameraX:** For camera operations and image analysis.
*   **TensorFlow Lite:** For on-device machine learning inference.
*   **Room Persistence Library:** For storing translation history.
*   **Material Components:** For UI elements.

## ⚙️ Setup and Installation

&nbsp; To build and run this project, follow these steps:

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/Akshay-86/Sign_Translator.git
    ```
2.  **Open in Android Studio:**
    *   Open Android Studio.
    *   Click on `File` > `Open` and select the cloned project directory.
3.  **Build and Run:**
    *   Let Android Studio sync the Gradle files.
    *   Connect an Android device or start an emulator.
    *   Click the `Run 'app'` button.

## 👨‍💻 Contributors
- [**Ch. Vindhya**](https://github.com/vindhya0208) - UI Designing.
- **Chat Gpt,Gemini 2.5** - General Assistance in Coding.
- **And all my teammates** - Data Gathering.

## 🙏 Acknowledgements
- Pretrained model used in this project [From this repo](https://github.com/idaraabasiudoh/American-Sign-Language-ASL-Detection/) [asl_model.tflite] — by [**Idara-Abasi Udoh**](https://github.com/idaraabasiudoh)

## 📜 Contributing
Contributions are welcome! Please feel free to submit a pull request or open an issue. But please credit the original authors.

&nbsp;
##### * This project is developed for educational purposes only [for my education purpose😁 - Collage project].