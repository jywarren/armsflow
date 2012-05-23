class Transfer < ActiveRecord::Base
  def agent
    return Agent.find(self.agent_id)
  end
  def recipient
    return Agent.find(self.recipient_id)
  end
end
