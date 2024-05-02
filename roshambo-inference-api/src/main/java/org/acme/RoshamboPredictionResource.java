package org.acme;


import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;

import ai.djl.translate.TranslateException;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Path("/")
public class RoshamboPredictionResource {

    @Inject
    ImageFactory imageFactory;

    @Inject
    RoshamboService roshamboService;

    @POST
    @Path("predictions")
    public HandPrediction hello(HandImage handImage) throws IOException, TranslateException {

        Image image = imageFactory.fromInputStream(new ByteArrayInputStream(handImage.getImage()));

        Classifications result = roshamboService.predict(image);

        HandPrediction handPrediction = new HandPrediction(result.best().getClassName());
        System.out.println(result);

        return handPrediction;
    }
}
