package com.jws.jwsapi.core.printer.utils;

import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.printer.discovery.DiscoveredPrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SelectedPrinterManager {
	private static final int MAX_HISTORY_SIZE = 5;

	private static final List<DiscoveredPrinter> selectedPrinterHistory = new ArrayList<DiscoveredPrinter>();

	public static DiscoveredPrinter getSelectedPrinter() {
		selectedPrinterHistory.size();
		if (selectedPrinterHistory.size() > 0) {

			return selectedPrinterHistory.get(0);
		} else {

			return null;
		}
	}

	public static void setSelectedPrinter(DiscoveredPrinter printer) {
		for(int i = 0; i < selectedPrinterHistory.size(); i++) {
			if (selectedPrinterHistory.get(i).address.equals(printer.address)) {
				selectedPrinterHistory.remove(i);
				break;
			}
		}
		selectedPrinterHistory.add(0, printer);
		if (selectedPrinterHistory.size() > MAX_HISTORY_SIZE) {
			selectedPrinterHistory.remove(selectedPrinterHistory.size() - 1);
		}
	}

	public static Connection getPrinterConnection() {
		return Objects.requireNonNull(SelectedPrinterManager.getSelectedPrinter()).getConnection();
	}

}
