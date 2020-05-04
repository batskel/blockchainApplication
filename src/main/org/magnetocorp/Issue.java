package org.magnetocorp;/*
SPDX-License-Identifier: Apache-2.0
*/



import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import main.org.papernet.CommercialPaper;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.GatewayException;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Issue {

	private static final String ENVKEY="CONTRACT_NAME";

	public static void main(String[] args) {

		String contractName="trade11";
		// get the name of the contract, in case it is overridden
		Map<String,String> envvar = System.getenv();
		if (envvar.containsKey(ENVKEY)){
			contractName=envvar.get(ENVKEY);
		}

		Gateway.Builder builder = Gateway.createBuilder();

		try {
			// A wallet stores a collection of identities
			Path walletPath = Paths.get(".", "wallet");
			Wallet wallet = Wallets.newFileSystemWallet(walletPath);
			System.out.println("Read wallet info from: " + walletPath.toString());

			String userName = "User1@org1.example.com";

			Path connectionProfile = Paths.get("/Users/dmitrybatskel/blockchain/connection.yaml");

			// Set connection options on the gateway builder
			builder.identity(wallet, userName).networkConfig(connectionProfile).discovery(false);

			// Connect to gateway using application specified parameters
			try(Gateway gateway = builder.connect()) {

				// Access PaperNet network
				System.out.println("Use network channel: mychannel.");
				Network network = gateway.getNetwork("mychannel");

				// Get addressability to commercial paper contract
				System.out.println("Use org.papernet.commercialpaper smart contract.");
				Contract contract = network.getContract(contractName,"TradeItem");

				// Issue commercial paper
				System.out.println("Submit commercial paper issue transaction.");
				byte[] response = contract.submitTransaction("tradeItemWSExist", "12");

				// Process response
				System.out.println("Process issue transaction response."+new String(response, UTF_8));
//				CommercialPaper paper = CommercialPaper.deserialize(response);
//				System.out.println(paper);
			}
		} catch (GatewayException | IOException | TimeoutException | InterruptedException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
