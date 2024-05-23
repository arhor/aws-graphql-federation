package com.github.arhor.aws.graphql.federation.comments.api.graphql.datafetcher;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.USER;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.SwitchUserCommentsInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.User;
import com.github.arhor.aws.graphql.federation.comments.service.UserRepresentationService;
import com.github.arhor.aws.graphql.federation.tracing.Trace;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsEntityFetcher;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static com.github.arhor.aws.graphql.federation.common.MapExtKt.getUuid;

@Trace
@DgsComponent
@RequiredArgsConstructor
public class UserRepresentationFetcher {

    private final UserRepresentationService userService;

    /* ---------- Entity Fetchers ---------- */

    @DgsEntityFetcher(name = USER.TYPE_NAME)
    public User resolveUser(final Map<String, ?> values) {
        return userService.findUserRepresentation(
            getUuid(
                values,
                USER.Id
            )
        );
    }

    /* ---------- Mutations ---------- */

    @DgsMutation
    public boolean switchUserComments(final @InputArgument SwitchUserCommentsInput input) {
        return userService.switchComments(input);
    }
}
