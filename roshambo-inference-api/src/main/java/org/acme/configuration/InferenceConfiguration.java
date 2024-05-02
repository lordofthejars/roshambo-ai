package org.acme.configuration;

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
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.Translator;

import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import java.io.IOException;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Singleton
public class InferenceConfiguration {

    @ConfigProperty(name = "model.path")
    private Optional<Path> modelPath;

    private static final List<String> CLASSES = Arrays.asList("rock", "paper", "scissors");

    @Produces
    public ImageFactory imageFactory() {
        return ImageFactory.getInstance();
    }

    private ZooModel<Image, Classifications> zooModel;

    @Startup
    public void initializeModel() throws ModelNotFoundException, MalformedModelException, IOException {

        final Translator<Image, Classifications> translator = getTranslator();
        final Criteria<Image, Classifications> criteria = getCriteria(translator);
        this.zooModel = criteria.loadModel();

    }

    //@Produces
    public Translator<Image, Classifications> getTranslator() {
        return
            ImageClassificationTranslator.builder()
                .addTransform(a -> NDImageUtils.resize(a, 150).div(255.0f))
                .optSynset(CLASSES)
                .build();
    }

    //@Produces
    Criteria<Image, Classifications> getCriteria(Translator<Image, Classifications> translator) {

        System.out.println("Create Criteria");

        final Criteria.Builder<Image, Classifications> imageClassificationsBuilder = Criteria.builder()
            .setTypes(Image.class, Classifications.class);

        if (modelPath.isEmpty()) {
            String modelLoction = Thread.currentThread()
                .getContextClassLoader()
                .getResource("model/model.onnx").toExternalForm();

            imageClassificationsBuilder
                .optModelUrls(modelLoction);
        } else {
            imageClassificationsBuilder
                .optModelPath(modelPath.get());
        }

        return
                imageClassificationsBuilder
                .optEngine("OnnxRuntime")
                .optModelName("rps")
                .optTranslator(translator)
                    .optProgress(new ProgressBar())
                .build();
    }

    @Produces
    ZooModel<Image, Classifications> getZooModel() {
        return this.zooModel;
    }

    @Produces
    @RequestScoped
    public Predictor<Image, Classifications> predictor(ZooModel<Image, Classifications>  zooModel) {
        System.out.println("Create Predictor");
        return zooModel.newPredictor();
    }

    void close(@Disposes Predictor<Image, Classifications>  predictor) {
        System.out.println("Closes Predictor");
        predictor.close();
    }


}
