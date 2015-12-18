/* 
 * Copyright (C) 2015 DECOIT GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.decoit.siemgui.service.converter;

import de.decoit.siemgui.domain.NewTicket;
import de.decoit.siemgui.domain.Ticket;
import de.decoit.siemgui.domain.tickethistory.TicketHistoryItem;
import de.decoit.siemgui.domain.TicketQueue;
import de.decoit.siemgui.domain.TicketStatus;
import de.decoit.siemgui.domain.User;
import de.decoit.siemgui.domain.tickethistory.AnsweredTicketHistoryItem;
import de.decoit.siemgui.domain.tickethistory.CommentedTicketHistoryItem;
import de.decoit.siemgui.domain.tickethistory.ContentText;
import de.decoit.siemgui.domain.tickethistory.ContentTextLine;
import de.decoit.siemgui.domain.tickethistory.ContentTextQuote;
import de.decoit.siemgui.domain.tickethistory.CreatedTicketHistoryItem;
import de.decoit.siemgui.domain.tickethistory.FieldChangedTicketHistoryItem;
import de.decoit.siemgui.domain.tickethistory.StatusChangedTicketHistoryItem;
import de.decoit.siemgui.domain.tickethistory.TakenTicketHistoryItem;
import de.decoit.siemgui.exception.ConversionException;
import de.decoit.siemgui.exception.ExternalServiceException;
import de.decoit.siemgui.exception.LambdaExternalServiceException;
import de.decoit.siemgui.exception.SkipConversionException;
import de.decoit.siemgui.security.AuthorizedUser;
import de.decoit.siemgui.service.TicketService;
import de.decoit.siemgui.service.UserDetailsService;
import de.decoit.rt.model.RtQueue;
import de.decoit.rt.model.RtTicket;
import de.decoit.rt.model.RtTicket.RtTicketStatus;
import de.decoit.rt.model.RtTicketHistoryItem;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * This implementation of TicketManagementConverter can be used to convert between RtTicket, RtQueue and RtTicketHistoryItem objects
 * and their corresponding domain objects.
 * RtTicket, RtQueue and RtTicketHistoryItem objects are used by the DECOIT RT Connector.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Service
public class RtTicketManagementConverter implements TicketManagementConverter<RtTicket, RtQueue, RtTicketHistoryItem> {
	private final Logger LOG = LoggerFactory.getLogger(RtTicketManagementConverter.class.getName());

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private TicketService ticketServ;

	@Autowired
	private DateConverter dateConv;


	@Override
	public RtTicket convertDomTicket(Ticket ticket) throws ConversionException {
		if (ticket == null) {
			throw new ConversionException("Received null pointer for Ticket conversion");
		}

		RtTicket rtt = new RtTicket();

		rtt.setId(ticket.getId());
		rtt.setStatus(convertDomTicketStatus(ticket.getStatus()));
		rtt.setPriority(ticket.getPriority());
		rtt.setSubject(ticket.getTitle());
		rtt.setCreator(ticket.getCreator());
		rtt.setCreated(dateConv.localDateTimeToDate(ticket.getCreatedOn()));
		rtt.setOwner(ticket.getOwner().getUsername());
		rtt.setRequestors(ticket.getRequestors());
		rtt.setTimeWorked(ticket.getTimeWorked());
		rtt.setResolved(dateConv.localDateTimeToDate(ticket.getResolvedOn()));
		rtt.setQueue(ticket.getQueue().getName());
		rtt.addCustomField("Risk", Integer.toString(ticket.getRisk(), 10));
		rtt.addCustomField("Incident", Long.toString(ticket.getIncidentId()));
		rtt.setDue(dateConv.localDateTimeToDate(ticket.getDueOn()));

		return rtt;
	}


	@Override
	public List<RtTicket> convertDomTicketList(List<Ticket> ticketList) throws ConversionException {
		if (ticketList == null) {
			throw new ConversionException("Received null pointer for List<Ticket> conversion");
		}

		List<RtTicket> domList = ticketList.stream().map(t -> this.convertDomTicket(t)).collect(Collectors.toList());

		return domList;
	}


	@Override
	public RtTicket convertDomNewTicket(NewTicket ticket) throws ConversionException {
		if (ticket == null) {
			throw new ConversionException("Received null pointer for NewTicket conversion");
		}

		List<String> requestors = new ArrayList<>();
		requestors.add(ticket.getRequestor());

		RtTicket rtt = new RtTicket();

		rtt.setSubject(ticket.getTitle());
		rtt.setQueue(ticket.getQueue().getName());
		rtt.setStatus(convertDomTicketStatus(ticket.getStatus()));
		rtt.setText(ticket.getText());
		rtt.setRequestors(requestors);
		rtt.setStarts(dateConv.localDateTimeToDate(ticket.getStarts()));
		rtt.addCustomField("Risk", Integer.toString(ticket.getRisk(), 10));
		rtt.addCustomField("Incident", Long.toString(ticket.getIncidentId()));
		rtt.setDue(dateConv.localDateTimeToDate(ticket.getDueOn()));

		return rtt;
	}


	@Override
	public List<RtTicket> convertDomNewTicketList(List<NewTicket> ticketList) throws ConversionException {
		if (ticketList == null) {
			throw new ConversionException("Received null pointer for List<NewTicket> conversion");
		}

		List<RtTicket> domList = ticketList.stream().map(t -> this.convertDomNewTicket(t)).collect(Collectors.toList());

		return domList;
	}


	@Override
	public TicketQueue convertQueue(RtQueue queue) throws ConversionException {
		if (queue == null) {
			throw new ConversionException("Received null pointer for RtQueue conversion");
		}

		TicketQueue tq = new TicketQueue();

		tq.setId(queue.getId());
		tq.setName(queue.getName());
		tq.setDescription(queue.getDescription());

		return tq;
	}


	@Override
	public List<TicketQueue> convertQueueList(List<RtQueue> queueList) throws ConversionException {
		if (queueList == null) {
			throw new ConversionException("Received null pointer for List<RtQueue> conversion");
		}

		List<TicketQueue> domList = queueList.stream().map(t -> this.convertQueue(t)).collect(Collectors.toList());

		return domList;
	}


	@Override
	public Ticket convertTicket(RtTicket ticket, AuthorizedUser user) throws ConversionException, ExternalServiceException {
		if (ticket == null) {
			throw new ConversionException("Received null pointer for RtTicket conversion");
		}

		Map<String, String> customFields = ticket.getCustomFields();
		int risk;
		long incident;

		try {
			risk = Integer.valueOf(customFields.getOrDefault("Risk", "0"));
			incident = Long.valueOf(customFields.get("Incident"));
		}
		catch (NumberFormatException ex) {
			throw new ConversionException("Custom field conversion of RtTicket failed", ex);
		}

		Ticket domTicket = new Ticket();

		domTicket.setId(ticket.getId());
		domTicket.setStatus(convertTicketStatus(ticket.getStatus()));
		domTicket.setPriority(ticket.getPriority());
		domTicket.setTitle(ticket.getSubject());
		domTicket.setCreator(ticket.getCreator());
		domTicket.setCreatedOn(dateConv.dateToLocalDateTime(ticket.getCreated()));
		domTicket.setOwner(userDetailsService.getUserDetails(ticket.getOwner(), user));
		domTicket.setRisk(risk);
		domTicket.setRequestors(ticket.getRequestors());
		domTicket.setTimeWorked(ticket.getTimeWorked());
		domTicket.setResolvedOn(dateConv.dateToLocalDateTime(ticket.getResolved()));
		domTicket.setIncidentId(incident);
		domTicket.setQueue(ticketServ.getTicketQueueDetails(ticket.getQueue(), user));
		domTicket.setDueOn(dateConv.dateToLocalDateTime(ticket.getDue()));

		return domTicket;
	}


	@Override
	public List<Ticket> convertTicketList(List<RtTicket> ticketList, AuthorizedUser user) throws ConversionException, ExternalServiceException {
		if (ticketList == null) {
			throw new ConversionException("Received null pointer for List<RtTicket> conversion");
		}

		try {
			List<Ticket> domList = ticketList.stream().map(t -> this.lambdaConvertTicket(t, user)).collect(Collectors.toList());

			return domList;
		}
		catch (LambdaExternalServiceException ex) {
			ExternalServiceException esex = (ExternalServiceException) ex.getCause();
			throw esex;
		}
	}


	@Override
	public TicketHistoryItem convertTicketHistory(RtTicketHistoryItem historyItem, AuthorizedUser user) throws ConversionException, ExternalServiceException {
		if (historyItem == null) {
			throw new ConversionException("Received null pointer for RtTicketHistoryItem conversion");
		}

		TicketHistoryItem thi;

		switch(historyItem.getType()) {
			case CREATE:
				thi = buildCreatedTicketHistoryItem(historyItem);
				break;
			case STATUS:
				thi = buildStatusChangedTicketHistoryItem(historyItem);
				break;
			case SET:
				// If field is "Owner", create a TakenTicketHistoryItem object.
				// Otherwise drop into the CUSTOM_FIELD case and create a FieldChangedTicketHistoryItem object.
				if(historyItem.getField().equals("Owner")) {
					thi = buildTakenTicketHistoryItem(historyItem, user);
					break;
				}
			case CUSTOM_FIELD:
				thi = buildFieldChangedTicketHistoryItem(historyItem);
				break;
			case CORRESPOND:
				thi = buildAnsweredTicketHistoryItem(historyItem);
				break;
			case COMMENT:
				thi = buildCommentedTicketHistoryItem(historyItem);
				break;
			default:
				// An unsupported history item type will raise a SkipConversionException here and thus make this item ignored
				throw new SkipConversionException("Unsupported RT ticket history item: " + historyItem.getType());
		}

		thi = fillGeneralTicketHistoryProperties(historyItem, user, thi);

		return thi;
	}


	@Override
	public List<TicketHistoryItem> convertTicketHistoryList(List<RtTicketHistoryItem> historyItemList, AuthorizedUser user) throws ConversionException, ExternalServiceException {
		if (historyItemList == null) {
			throw new ConversionException("Received null pointer for List<RtTicketHistoryItem> conversion");
		}

		try {
			List<TicketHistoryItem> domList = historyItemList.stream().map(t -> this.lambdaConvertTicketHistory(t, user)).filter(t -> t != null).collect(Collectors.toList());

			return domList;
		}
		catch (LambdaExternalServiceException ex) {
			ExternalServiceException esex = (ExternalServiceException) ex.getCause();
			throw esex;
		}
	}


	/**
	 * Wrap the convertTicket(sessionId, ticket) calls for use in lambda expressions.
	 * The possible ExternalServiceException will be caught and wrapped into an unchecked LambdaExternalServiceException.
	 * I is highly recommended to extract the ExternalServiceException from that unchecked exception outside the lambda expression
	 * and handle it accordingly.
	 *
	 * @param sessionId Session ID for ticket management access
	 * @param ticket    Input object
	 * @return The created ticket object
	 *
	 * @throws LambdaExternalServiceException if an ExternalServiceException is caught
	 */
	private Ticket lambdaConvertTicket(RtTicket ticket, AuthorizedUser user) throws LambdaExternalServiceException {
		try {
			Ticket t = this.convertTicket(ticket, user);
			return t;
		}
		catch (ExternalServiceException ex) {
			throw new LambdaExternalServiceException(ex);
		}
	}


	/**
	 * Wrap the convertTicketHistory(sessionId, historyItem) calls for use in lambda expressions.
	 * The possible ExternalServiceException will be caught and wrapped into an unchecked LambdaExternalServiceException.
	 * I is highly recommended to extract the ExternalServiceException from that unchecked exception outside the lambda expression
	 * and handle it accordingly.
	 *
	 * @param sessionId   Session ID for ticket management access
	 * @param historyItem Input object
	 * @return The created TicketHistory object
	 *
	 * @throws LambdaExternalServiceException if an ExternalServiceException is caught
	 */
	private TicketHistoryItem lambdaConvertTicketHistory(RtTicketHistoryItem historyItem, AuthorizedUser user) throws LambdaExternalServiceException {
		try {
			TicketHistoryItem thi = this.convertTicketHistory(historyItem, user);
			return thi;
		}
		catch (SkipConversionException ex) {
			return null;
		}
		catch (ExternalServiceException ex) {
			throw new LambdaExternalServiceException(ex);
		}
	}


	/**
	 * Convert the ticket status enum of the RT connector to the general ticket status enum.
	 * Any ticket status other than new, open or resolved will result in a domain ticket status of UNKNOWN.
	 *
	 * @param rts RT connector ticket status
	 * @return General ticket status
	 */
	private TicketStatus convertTicketStatus(RtTicket.RtTicketStatus rts) {
		switch (rts) {
			case NEW:
				return TicketStatus.NEW;
			case OPEN:
				return TicketStatus.OPEN;
			case RESOLVED:
				return TicketStatus.RESOLVED;
			default:
				StringBuilder logSb = new StringBuilder("Conversion attempt for unsupported RT ticket status: ");
				logSb.append(rts);

				LOG.warn(logSb.toString());

				return TicketStatus.UNKNOWN;
		}
	}


	/**
	 * Convert the ticket status enum of the RT connector to the general ticket status enum.
	 * Any ticket status other than new, open or resolved will result in a domain ticket status of UNKNOWN.
	 *
	 * @param rts RT connector ticket status
	 * @return General ticket status
	 */
	private TicketStatus convertTicketStatus(String status) {
		RtTicketStatus rts = RtTicketStatus.fromStatusText(status);

		switch (rts) {
			case NEW:
				return TicketStatus.NEW;
			case OPEN:
				return TicketStatus.OPEN;
			case RESOLVED:
				return TicketStatus.RESOLVED;
			default:
				StringBuilder logSb = new StringBuilder("Conversion attempt for unsupported RT ticket status: ");
				logSb.append(rts);

				LOG.warn(logSb.toString());

				return TicketStatus.UNKNOWN;
		}
	}


	/**
	 * Convert the ticket status enum of the RT connector to the general ticket status enum.
	 *
	 * @param ts General ticket status
	 * @return RT connector ticket status
	 *
	 * @throws ConversionException if type cannot be converted
	 */
	private RtTicket.RtTicketStatus convertDomTicketStatus(TicketStatus ts) {
		switch (ts) {
			case NEW:
				return RtTicket.RtTicketStatus.NEW;
			case OPEN:
				return RtTicket.RtTicketStatus.OPEN;
			case RESOLVED:
				return RtTicket.RtTicketStatus.RESOLVED;
			default:
				throw new ConversionException("Conversion attempt (dom->spec) for unsupported ticket status: " + ts);
		}
	}


	/**
	 * Create a ticket history item that represents the event of ticket creation.
	 * This method only fills the type specific properties, general properties need to be
	 * added afterwards.
	 *
	 * @param thi Ticket history item from RT
	 * @return Created history item object
	 */
	private CreatedTicketHistoryItem buildCreatedTicketHistoryItem(RtTicketHistoryItem thi) {
		CreatedTicketHistoryItem cthi = new CreatedTicketHistoryItem();

		cthi.setText(convertContentText(thi.getContent()));

		return cthi;
	}


	/**
	 * Create a ticket history item that represents the event of owner change.
	 * This method only fills the type specific properties, general properties need to be
	 * added afterwards.
	 *
	 * @param thi Ticket history item from RT
	 * @param user Current authorized user containing ticket system authentiation information
	 * @return Created history item object
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	private TakenTicketHistoryItem buildTakenTicketHistoryItem(RtTicketHistoryItem thi, AuthorizedUser user) throws ExternalServiceException {
		long newOwnerId = Long.valueOf(thi.getNewValue());

		TakenTicketHistoryItem cthi = new TakenTicketHistoryItem();

		User u = userDetailsService.getUserDetailsById(newOwnerId, user);
		cthi.setNewOwner(u);

		return cthi;
	}


	/**
	 * Create a ticket history item that represents a comment on the ticket.
	 * This method only fills the type specific properties, general properties need to be
	 * added afterwards.
	 *
	 * @param thi Ticket history item from RT
	 * @return Created history item object
	 */
	private CommentedTicketHistoryItem buildCommentedTicketHistoryItem(RtTicketHistoryItem thi) {
		CommentedTicketHistoryItem cthi = new CommentedTicketHistoryItem();

		if(LOG.isDebugEnabled()) {
			LOG.debug("Comment content:");
			LOG.debug(thi.getContent());
		}
		cthi.setText(convertContentText(thi.getContent()));

		return cthi;
	}


	/**
	 * Create a ticket history item that represents an answer on the ticket.
	 * This method only fills the type specific properties, general properties need to be
	 * added afterwards.
	 *
	 * @param thi Ticket history item from RT
	 * @return Created history item object
	 */
	private AnsweredTicketHistoryItem buildAnsweredTicketHistoryItem(RtTicketHistoryItem thi) {
		AnsweredTicketHistoryItem athi = new AnsweredTicketHistoryItem();

		if(LOG.isDebugEnabled()) {
			LOG.debug("Answer content:");
			LOG.debug(thi.getContent());
		}
		athi.setText(convertContentText(thi.getContent()));

		return athi;
	}


	/**
	 * Create a ticket history item that represents the event of a status change.
	 * This method only fills the type specific properties, general properties need to be
	 * added afterwards.
	 *
	 * @param thi Ticket history item from RT
	 * @return Created history item object
	 */
	private StatusChangedTicketHistoryItem buildStatusChangedTicketHistoryItem(RtTicketHistoryItem thi) {
		StatusChangedTicketHistoryItem scthi = new StatusChangedTicketHistoryItem();

		scthi.setOldStatus(convertTicketStatus(thi.getOldValue()));
		scthi.setNewStatus(convertTicketStatus(thi.getNewValue()));

		return scthi;
	}


	/**
	 * Create a ticket history item that represents the event of a field value change.
	 * This method only fills the type specific properties, general properties need to be
	 * added afterwards.
	 *
	 * @param thi Ticket history item from RT
	 * @return Created history item object
	 */
	private FieldChangedTicketHistoryItem buildFieldChangedTicketHistoryItem(RtTicketHistoryItem thi) {
		FieldChangedTicketHistoryItem fcthi = new FieldChangedTicketHistoryItem();

		switch (thi.getType()) {
			case CUSTOM_FIELD:
				// For custom field events some additional work is neccessary
				// Description always follows the scheme "FIELD VALUE added by USER"
				// So we need to find the VALUE in the string and then extract the substring from 0 to the VALUE string
				// to get the name of the field

				// Encapsulate the value in spaces and the "added by" phrase so we prevent false positives in the field name
				StringBuilder valueSb = new StringBuilder(" ");
				valueSb.append(thi.getNewValue());
				valueSb.append(" added by ");

				// Search for the above phrase in the description
				int index = thi.getDescription().indexOf(valueSb.toString());

				// The field name is the substring from index 0 to the beginning of the above phrase
				String field = thi.getDescription().substring(0, index).trim();

				fcthi.setField(field);

				if (LOG.isDebugEnabled()) {
					LOG.debug("Custom field: Set field to: " + field);
				}

				break;
			default:
				fcthi.setField(thi.getField());
		}

		fcthi.setOldValue(thi.getOldValue());
		fcthi.setNewValue(thi.getNewValue());

		return fcthi;
	}


	/**
	 * Fill the general properties of a specified ticket history item object.
	 * This method fills all general properties of a ticket history item object. Those are
	 * all properties that are member of the abstract TicketHistoryItem superclass.
	 *
	 * @param rtthi Ticket history item from RT
	 * @param user Current authorized user containing ticket system authentiation information
	 * @param thi Object to fill with the general information
	 * @return The object provided in <i>thi</i> with all general fields filled
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	private TicketHistoryItem fillGeneralTicketHistoryProperties(RtTicketHistoryItem rtthi, AuthorizedUser user, TicketHistoryItem thi) throws ExternalServiceException {
		long id = rtthi.getId();
		long ticketId = rtthi.getTicketId();
		LocalDateTime createdOn = dateConv.gmtDateToLocalDateTime(rtthi.getCreated());
		User createdBy = userDetailsService.getUserDetails(rtthi.getCreator(), user);
		String title = rtthi.getDescription();
		int timeTaken = rtthi.getTimeTaken();

		thi.setId(id);
		thi.setTicketId(ticketId);
		thi.setDate(createdOn);
		thi.setCreator(createdBy);
		thi.setTitle(title);
		thi.setTimeTaken(timeTaken);

		return thi;
	}


	/**
	 * Convert the content text from a ticket history item into and object structure.
	 * The resulting structure is able to represent normal text lines and quotes included in the text.
	 *
	 * @param text The content of the history item
	 * @return An object containing the resulting structure
	 */
	private ContentText convertContentText(String text) {
		ContentText ct = new ContentText();

		// First group captures the spaces used for indentation
		// Second group captures the text line itself
		Pattern p = Pattern.compile("^(\\s*)(\\S.*)$");

		Integer currentIndentDepth = 0;
		Map<Integer, ContentTextQuote> quoteMap = new HashMap<>();
		for(String line : text.split("\n")) {
			Matcher m = p.matcher(line);
			if(m.matches()) {
				if(m.group(1).length() > currentIndentDepth) {
					// If indentation is larger than before, we have to begin a new quote
					ContentTextQuote newQuote = new ContentTextQuote();

					newQuote.addElement(new ContentTextLine(m.group(2)));

					if(currentIndentDepth == 0) {
						ct.addElement(newQuote);
					}
					else {
						ContentTextQuote currentQuote = quoteMap.get(currentIndentDepth);
						currentQuote.addElement(newQuote);
					}

					currentIndentDepth = m.group(1).length();
					quoteMap.put(currentIndentDepth, newQuote);
				}
				else {
					if(m.group(1).length() < currentIndentDepth) {
						quoteMap.remove(currentIndentDepth);
						currentIndentDepth = m.group(1).length();
					}

					if(currentIndentDepth == 0) {
						ct.addElement(new ContentTextLine(m.group(2)));
					}
					else {
						ContentTextQuote currentQuote = quoteMap.get(currentIndentDepth);
						currentQuote.addElement(new ContentTextLine(m.group(2)));
					}
				}
			}
		}

		return ct;
	}
}
