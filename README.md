# words-wordsserver
Aplicación que permite convertir un audio a texto, lista los textos convertidos y las palabras usadas en estos, para ello expone 3 
servicios REST utilizando Spring Boot


# Servicio /upload
Recibe un parámetro llamado 'audioURL' que especifica la url de algún audio en cualquiera de estos formatos
"wav", "webm", "l16", "basic", "flac", "mulaw", "mp3", "mpeg", "l16".

# Servicio /getSpeeches
Retorna el texto de los audios convertidos

# Servicio /getUsedWords
Retorna las palabras usadas en los textos traducidos con un contador que indica el número de veces que esta palabra se ha repetido.
