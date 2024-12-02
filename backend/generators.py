import requests
import os
import glob
from groq import Groq

IMAGE_MODEL_URL = "https://image.pollinations.ai/prompt/"
TTS_MODEL_URL = "https://translate.google.com/translate_tts?ie=UTF-8&tl=tr-TR&client=tw-ob"

class ImageDescriptionGenerator:
    """
    A class used to generate detailed text prompts for a text-to-image AI model.
    Methods
    -------
    __init__():
        Initializes the ImageDescriptionGenerator with a language model.
    generate(word: str) -> str:
        Generates a highly detailed and optimized text prompt for a given word.
    """
    
    def __init__(self):
        self.llm_client = Groq()
        
    def generate(self, word):
        completion = self.llm_client.chat.completions.create(
            model="llama3-70b-8192",
            messages=[
                {
                    "role": "user",
                    "content":  f"create a highly detailed text prompt for a text-to-image AI model " + 
                                f"to illustrate the most common meaning of the word {word}, in 2D. " +
                                "Focus on being descriptive, specific, and accurate. " +
                                "Avoid using abstract terms. "
                                "Then optimize the prompt. " +  
                                "Output ONLY the final optimized prompt with no additional explanation or introductory text."
                }
            ],
            temperature=1,
            max_tokens=1024,
            top_p=1,
            stream=True,
            stop=None,
        )
        
        response = ""
        for chunk in completion:
            response += chunk.choices[0].delta.content or ""
            
        return response



class ImageGenerator:
    """
    A class used to generate images based on a description using an external API.
    Attributes
    ----------
    api_url (str): The URL of the image generation API.
    image_id (int): A counter to keep track of the generated image filenames.
    Methods
    -------
    generate(description)
        Generates an image based on the provided description and saves it to a file.
    clean()
        Deletes all generated image files from the 'images' directory.
    """
    
    def __init__(self):
        self.api_url = IMAGE_MODEL_URL
        self.image_id = 0

    def generate(self, description):
        request_url = self.api_url + description
        response = requests.get(request_url)
        if response.status_code == 200:
            img_filename = f"images/{self.image_id}.png"
            self.image_id += 1
            with open(img_filename, 'wb') as f:
                f.write(response.content)
            return img_filename
        
        raise ValueError("Failed to generate image")
    
    def clean(self):
        image_files = glob.glob('images/*.png')
        for file in image_files:
            os.remove(file)
            
            
class PronunciationAudioGenerator:
    """
    A class to generate pronunciation audio files using a text-to-speech API.
    Attributes:
        api_url (str): The URL of the text-to-speech API.
        audio_id (int): A counter to keep track of the generated audio filenames.
    Methods:
        generate(word):
            Generates an audio file for the given word using the text-to-speech API.
            Args:
                word (str): The word to generate the pronunciation audio for.
            Returns:
                str: The filename of the generated audio file.
            Raises:
                ValueError: If the audio generation fails.
        clean():
            Deletes all generated audio files in the 'audio' directory.
    """
    def __init__(self):
        self.api_url = TTS_MODEL_URL
        self.audio_id = 0
    
    def generate(self, word):
        request_url = self.api_url + "&q=" + word
        response = requests.get(request_url)
        if response.status_code == 200:
            audio_filename = f"audio/{self.audio_id}.mp3"
            self.audio_id += 1
            with open(audio_filename, 'wb') as f:
                f.write(response.content)
            return audio_filename
        
        raise ValueError("Failed to generate audio")


    def clean(self):
        audio_files = glob.glob('audio/*.mp3')
        for file in audio_files:
            os.remove(file)
        
    


