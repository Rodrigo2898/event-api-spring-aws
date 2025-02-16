package com.rr_eventtec.api.service;

import com.rr_eventtec.api.domain.event.Event;
import com.rr_eventtec.api.domain.event.EventRequestDTO;

public interface IEventService {

    Event createEvent(EventRequestDTO data);
}
