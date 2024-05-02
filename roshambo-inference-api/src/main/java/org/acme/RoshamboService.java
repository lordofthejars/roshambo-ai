package org.acme;

import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.translate.TranslateException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class RoshamboService {

    @Inject
    Predictor<Image, Classifications> predictor;

    public Classifications predict(Image image) throws TranslateException {
        return predictor.predict(image);
    }
}
