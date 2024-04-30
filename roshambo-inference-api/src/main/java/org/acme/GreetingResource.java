package org.acme;

import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import ai.djl.modality.cv.util.NDImageUtils;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Path("/hello")
public class GreetingResource {

    private static final List<String> CLASSES = Arrays.asList("rock", "paper", "scissors");

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() throws ModelNotFoundException, MalformedModelException, IOException, TranslateException {

        Image image = ImageFactory.getInstance().fromFile(Paths.get("src/main/resources/sc.jpeg"));
        java.nio.file.Path modelPath = Paths.get("model/model.onnx ");

        Translator<Image, Classifications> translator =
                ImageClassificationTranslator.builder()
                        .addTransform(a -> NDImageUtils.resize(a, 150).div(255.0f))
                        .optSynset(CLASSES)
                        .build();

        Criteria<Image, Classifications> criteria =
                Criteria.builder()
                        .setTypes(Image.class, Classifications.class)
                        .optModelPath(modelPath)
                        .optEngine("OnnxRuntime")
                        .optModelName("rps")
                        .optTranslator(translator)
                        .build();

        try (ZooModel<Image, Classifications> model = criteria.loadModel();
             Predictor<Image, Classifications> predictor = model.newPredictor()) {
            Classifications result = predictor.predict(image);
            System.out.println(result);
        }

        return "Hello from Quarkus REST";
    }
}
