import os


def empty_folder(folder_path):
    for item in os.listdir(folder_path):
        item_path = os.path.join(folder_path, item)
        if os.path.isfile(item_path):
            os.remove(item_path)
        elif os.path.isdir(item_path):
            empty_folder(item_path)
            os.rmdir(item_path)


def save_file(file, userID):
    file_location = f"docs/{userID}/{file.filename}"

    try:
        os.makedirs(f"docs/{userID}")
    except FileExistsError:
        pass

    with open(file_location, "wb") as f:
        contents = file.file.read()
        f.write(contents)
