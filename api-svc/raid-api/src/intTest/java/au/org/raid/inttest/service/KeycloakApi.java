package au.org.raid.inttest.service;

import au.org.raid.inttest.dto.keycloak.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "keycloak", url = "${keycloak.url}")
public interface KeycloakApi {
    @RequestMapping(method = RequestMethod.POST, value = "/raid/raid-user")
    ResponseEntity<Object> addRaidUser(@RequestBody RaidUserPermissionsRequest user);

    @RequestMapping(method = RequestMethod.DELETE, value = "/raid/raid-user")
    ResponseEntity<Object> removeRaidUser(@RequestBody RaidUserPermissionsRequest user);

    @RequestMapping(method = RequestMethod.POST, value = "/raid/raid-admin")
    ResponseEntity<String> addRaidAdmin(@RequestBody RaidUserPermissionsRequest user);

    @RequestMapping(method = RequestMethod.GET, value = "/group/all")
    ResponseEntity<Groups> allGroups();

    @RequestMapping(method = RequestMethod.GET, value = "/group")
    ResponseEntity<Group> findById(@RequestParam final String groupId);

    @RequestMapping(method = RequestMethod.PUT, value = "/group/grant")
    ResponseEntity<String> grant(@RequestBody final Grant grant);

    @RequestMapping(method = RequestMethod.PUT, value = "/group/revoke")
    ResponseEntity<String> revoke(@RequestBody final Grant grant);

    @RequestMapping(method = RequestMethod.PUT, value = "/group/join")
    ResponseEntity<String> joinGroup(@RequestBody final GroupJoinRequest groupJoinRequest);

    @RequestMapping(method = RequestMethod.PUT, value = "/group/active-group")
    ResponseEntity<String> setActiveGroup(@RequestBody final ActiveGroupRequest groupJoinRequest);

    @RequestMapping(method = RequestMethod.GET, value = "/group/user-groups")
    ResponseEntity<List<Group>> getUserGroups();
}
