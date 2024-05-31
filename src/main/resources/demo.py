import torch
import librosa
import numpy as np
from models import MusicEmotionLSTM, LSTMPredictionModel
import sys
import os
from pathlib import Path

# Define cusomt exceptions
class AudioTooShort(Exception):
    pass
class SamplingRateError(Exception):
    pass

# Song class fro predition
class Song: 
    # Constructor either takes a librosa loaded audio clip or path
    # Sets up pytorch device
    def __init__(self, audio):
        if not((type(audio) is tuple) and (type(audio[0]) is np.ndarray)): 
            audio = librosa.load(audio, sr=44100)
        if audio[1] != 44100: 
            raise SamplingRateError("Please load your audio with sampling rate of 44.1kHz")
        self.audio, self.sr = audio

        if torch.backends.mps.is_available():
            self.device = torch.device("mps")
        elif torch.cuda.is_available(): 
            self.device = torch.device("cuda")
        else:
            self.device = torch.device("cpu")
        
        self.emotions=None
    
    # String representation of audio
    def __str__(self): 
        return self.audio

    # Takes loaded audio, does a mel spectrogram, and passes it to the model
    # Returns a tensor of the predicted arousal and valence vectors and sets them to self.emotions
    def getEmotions(self): 
        rem = len(self.audio) % 22050
        clips = []
        mels = []
        for i in range(int(len(self.audio) / 22050)): 
            start_time = rem + (i * 22050)
            end_time = start_time+22050
            clips.append(self.audio[start_time:end_time])

        for clip in clips: 
            mel_spectrogram = librosa.feature.melspectrogram(
                y=clip, sr=self.sr, n_fft=2048, hop_length=512, n_mels=128
            )
            log_mel_spectrogram = np.log1p(mel_spectrogram)
            mels.append(torch.transpose(torch.from_numpy(log_mel_spectrogram).to(self.device), 0, 1).unsqueeze(dim=0))
        
        del clips, rem

        input_tensor = torch.Tensor(len(mels), 44, 128).to(self.device)
        torch.cat(mels, out=input_tensor)

        del mels

        input_size = 128
        hidden_size = 20
        num_layers = 2
        output_size = 2

        home_directory = os.path.expanduser('~')
        cache_path = os.path.join(home_directory, 'Library', 'Caches')

        model = MusicEmotionLSTM(input_size, hidden_size, num_layers, output_size).to(self.device)
        model.load_state_dict(torch.load(os.path.join(cache_path, 'io.github.etashj.songsorter','models', 'model_state_dict.pth')))
        model.eval()

        out = model({"mel_data":input_tensor})

        self.emotions = out


        return out

# Ensure a filename is provided
if len(sys.argv) != 2:
    print("Usage: python process_file.py <filename>")
    sys.exit(1)

# The filename to read
filename = sys.argv[1]

s = Song(filename)
torch.set_printoptions(threshold=10_000)
print(s.getEmotions())
