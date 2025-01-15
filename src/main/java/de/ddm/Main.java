package de.ddm;

import akka.actor.typed.ActorSystem;
import de.ddm.actors.Guardian;
import de.ddm.configuration.Command;
import de.ddm.configuration.SystemConfiguration;
import de.ddm.singletons.SystemConfigurationSingleton;

import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		Command.applyOn(args);

		SystemConfiguration config = SystemConfigurationSingleton.get();

		final ActorSystem<Guardian.Message> guardian = ActorSystem.create(Guardian.create(), config.getActorSystemName(), config.toAkkaConfig());

		if (config.getRole().equals(SystemConfiguration.MASTER_ROLE)) {
			if (config.isStartPaused())
				waitForInput(">>> Press ENTER to start <<<");

			guardian.tell(new Guardian.StartMessage());

			 // This shutdown message must be removed to avoid the shutdown of the actor system before any work has been done.
			guardian.tell(new Guardian.ShutdownMessage());
		}
	}

	private static void waitForInput(String message) {
		try {
			System.out.println(message);
			System.in.read();
		} catch (IOException ignored) {
		}
	}
}
