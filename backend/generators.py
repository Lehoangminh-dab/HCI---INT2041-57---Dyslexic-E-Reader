from langchain_ollama import OllamaLLM
import requests

LANGUAGE_MODEL = "llama3.2:1b"
IMAGE_MODEL_URL = "https://image.pollinations.ai/prompt/"

class ImageDescriptionGenerator:
    def __init__(self):
        self.llm = OllamaLLM(model=LANGUAGE_MODEL)

    def generate(self, word):
        response = self.llm.invoke(
            f"create a highly detailed text prompt for a text-to-image AI model " + 
            f"to illustrate the most common meaning of the word {word}, in 2D. " +
            "Focus on being descriptive, specific, and accurate. " +
            "Avoid using abstract terms. "
            "Then optimize the prompt. " +  
            "Output ONLY the final optimized prompt with no additional explanation or introductory text."
        )

        return response   

class ImageGenerator:
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
            
    

