# words-wordsserver
Aplicación que permite convertir un audio a texto, lista los texto convertidos y las palabras usadas en estos, para ello expone 3 
servicios REST utilizando Spring Boot


# Servicio /upload
Recibe un parametro llamado 'audioURL' que especifica la url de algun audio en cualquiere de estos formatos
"wav", "webm", "l16", "basic", "flac", "mulaw", "mp3", "mpeg", "l16".

# Servicio /getSpeeches
Retorna el texto de los audios convertidos

# Servicio /getUsedWords
Retorna las palabras usadas en los texto traducidos con un contador que indica el numero de veces que esta palabra se a repetido.
