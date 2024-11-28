import generators

description_generator = generators.ImageDescriptionGenerator()
image_generator = generators.ImageGenerator()

def generate_image(word) -> str:
    description = description_generator.generate(word)
    img_filename = image_generator.generate(description)
    return img_filename

def generate_description(word) -> str:
    description = description_generator.generate(word)
    return description