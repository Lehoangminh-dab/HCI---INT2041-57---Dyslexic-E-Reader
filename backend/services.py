import generators
import atexit

description_generator = generators.ImageDescriptionGenerator()
image_generator = generators.ImageGenerator()
pronunciation_generator = generators.PronunciationAudioGenerator()

def generate_image(word) -> str:
    description = description_generator.generate(word)
    img_filename = image_generator.generate(description)
    return img_filename

def generate_description(word) -> str:
    description = description_generator.generate(word)
    return description

def generate_pronunciation(word) -> str:
    audio_filename = pronunciation_generator.generate(word)
    return audio_filename

def clean_up():
    image_generator.clean()
    pronunciation_generator.clean()

atexit.register(clean_up)