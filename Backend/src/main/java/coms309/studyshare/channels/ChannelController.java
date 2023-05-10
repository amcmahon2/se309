package coms309.studyshare.channels;

import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(value="Channel Management System", description="Operations pertaining to channels in the StudyShare app")
public class ChannelController {

    @Autowired
    private ChannelRepository channelRepository;

    //TODO: Add link to course and add permission check
    @Operation(summary = "Create a channel for a course in the StudyShare app (Gabe)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = ResponseEntity.class)
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "channel, the channel that is being created, is added to the channel repo and cleared, where finally a Http CREATED message occurs", required = true, dataType = "Channel", paramType = "RequestBody"),
    })
    @PostMapping("/channel/create")
    public ResponseEntity<?> createChannel(@RequestBody Channel channel) {

        channel.setMessages(null);
        channelRepository.save(channel);
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }


    //TODO: Add course link
    @Operation(summary = "List all channels (Gabe)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = List.class)
    })
    @GetMapping("/channel/listAll")
    public List<Channel> listChannels() {
        return channelRepository.findAll();
    }
}
