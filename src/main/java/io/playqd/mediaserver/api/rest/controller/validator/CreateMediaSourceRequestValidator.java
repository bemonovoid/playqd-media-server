package io.playqd.mediaserver.api.rest.controller.validator;

import io.playqd.mediaserver.api.rest.controller.request.CreateMediaSourceRequest;
import io.playqd.mediaserver.service.mediasource.MediaSource;
import io.playqd.mediaserver.service.mediasource.MediaSourceService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Optional;

@Slf4j
class CreateMediaSourceRequestValidator
        implements ConstraintValidator<ValidCreateMediaSourceRequest, CreateMediaSourceRequest> {

    private final MediaSourceService mediaSourceService;

    CreateMediaSourceRequestValidator(MediaSourceService mediaSourceService) {
        this.mediaSourceService = mediaSourceService;
    }

    @Override
    public boolean isValid(CreateMediaSourceRequest request, ConstraintValidatorContext context) {

        context.disableDefaultConstraintViolation();


        if (!pathIsValid(request, context)) {
            return false;
        }

        return validateNoSourceWithTheSamePath(request, context);
    }

    private boolean validateNoSourceWithTheSamePath(CreateMediaSourceRequest request,
                                                    ConstraintValidatorContext context) {

        var path = Paths.get(request.getPath());

        Optional<MediaSource> existingMediaSourceOpt = mediaSourceService.getAll().stream()
                .filter(aSource -> path.startsWith(aSource.path()))
                .findFirst();

        if (existingMediaSourceOpt.isPresent()) {
            var existingMediaSource = existingMediaSourceOpt.get();
            var message = String.format("Media source containing same path already exists. Source id: %s, path: %s",
                    existingMediaSource.id(), existingMediaSource.path());
            addErrorMessage(context, message);
            return false;
        }

        return true;
    }

    private boolean pathIsValid(CreateMediaSourceRequest request, ConstraintValidatorContext context) {
        try {
            var path = Paths.get(request.getPath());
            if (!Files.exists(path)) {
                String message = String.format("Path does not exist: %s", path);
                log.error(message);
                addErrorMessage(context, message);
                return false;
            }
            return true;
        } catch (InvalidPathException e) {
            var message = String.format("Path is invalid. Verify the path is correct: %s", request.getPath());
            log.error(message);
            addErrorMessage(context, message);
            return false;
        }
    }

    private void addErrorMessage(ConstraintValidatorContext context, String message) {
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
